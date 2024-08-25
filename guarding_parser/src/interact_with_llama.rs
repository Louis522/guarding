use reqwest::{Client, Error};
use serde::{Deserialize, Serialize};
use toml::de;
use std::fs::OpenOptions;
use std::{fs, io};
use std::collections::{HashMap, HashSet};
use std::io::Write;
use std::path::PathBuf;
use tokio;
use pest::Parser;
use serde::de::StdError;
use std::error::Error as St_Error; // 重命名 std::error::Error

use crate::api_output_history;

#[derive(Parser)]
#[grammar = "guarding.pest"]
struct IdentParser;

//配置
#[derive(Debug, Deserialize, Serialize)]
struct AppConfig {
    api_key: String,
    pre_command_file_path: String,
    guarding_file_path: String,
}


#[derive(Debug, Serialize)]
struct ApiRequest {
    model: String,
    messages: Vec<Message>,
    stream: bool,
}

#[derive(Debug, Serialize)]
struct InputData {
    messages: Vec<String>,
    stream: bool,
}

#[derive(Debug, Deserialize, Serialize)]
struct Message {
    role: String,
    content: String,
}

#[derive(Serialize)]
struct Parameters {
    result_format: String,
}

#[derive(Debug, Deserialize, Serialize)]
struct ApiResponse {
    model: String,
    created_at: String,
    message: Message,
    done: bool,
}

#[derive(Deserialize)]
struct Output {
    text: String,
    finish_reason: String,
}

fn load_config(file_path: &PathBuf) -> Result<AppConfig, Box<dyn std::error::Error>> {
    let contents = fs::read_to_string(file_path)?;
    Ok(de::from_str(&contents)?)
}

async fn post_api_request(request_body: &ApiRequest, client: &Client, api_url: &str) -> Result<ApiResponse, reqwest::Error> {
    let response = client.post(api_url)
        .json(request_body)
        .send()
        .await?
        .json::<ApiResponse>()
        .await;
    response
}

async fn call_api_and_write_to_file(pre_command_text: &str, input_text: &str, file_path: &str, client: &Client) -> Result<(), Box<dyn std::error::Error>> {
    let api_url = "http://localhost:11434/api/chat";
    let request_body = ApiRequest {
        model: "llama3".to_string(),
        messages: vec![
            Message { role: "user".to_string(), content: pre_command_text.to_string() },
            Message { role: "assistant".to_string(), content: "ok, I will answer you DSL result".to_string() },
            Message {
                role: "user".to_string(),
                content: format!("{}", input_text),
            },
        ],
        stream: false,
    };
    println!("Gnerating Architecture DSL...");
    let response = post_api_request(&request_body, client, api_url).await;
    if let Some(code_section) = extract_code_section(&response.unwrap().message.content) {
        let mut code_section_temp = code_section.to_string();
        //计数器
        const MAX_RETRIES: usize = 5;
        let mut attempts = 0;
        loop {
            for _ in 0..MAX_RETRIES {
                match check_semantic(&*code_section_temp, &*process_nl_string(&input_text)) {
                    Ok(_) => {
                        println!("Semantic is correct, proceeding to next step: check_syntax.");
                        break;
                    }
                    Err(err) => {
                        println!("Semantic error: {}. Retrying API request...", err);
                        println!("\x1b[33mNL:\x1b[0m{}\n\x1b[33mFirst_time_generated_DSL:\x1b[0m{}\n\x1b[33mDSL_{}:\x1b[0m{}", process_nl_string(&input_text), code_section, attempts,code_section_temp);
                        let request_body = ApiRequest {
                            model: "llama3".to_string(),
                            messages: vec![
                                Message { role: "user".to_string(), content: pre_command_text.to_string() },
                                Message { role: "assistant".to_string(), content: "ok, I will answer you DSL result".to_string() },
                                Message {
                                    role: "user".to_string(),
                                    content: format!("{}", input_text),
                                },
                                Message {
                                    role: "assistant".to_string(),
                                    content: format!("{}", code_section_temp),
                                },
                                Message {
                                    role: "user".to_string(),
                                    content: format!(r#"
The DSL you just generated is incorrect, Please regenerate: "{}"
only DSL. The wrong message is: {}"#, input_text, err),
                                },
                            ],
                            stream: false,
                        };

                        // println!("{:?}:{}", request_body,code_section);
                        let mut response = post_api_request(&request_body, client, api_url).await;
                        code_section_temp = extract_code_section(&response.unwrap().message.content).unwrap().parse().unwrap();
                        // code_section_temp = code_section.to_string();

                    }
                }
            }
            match check_syntax(&*code_section_temp) {
                Ok(_) => {
                    println!("Syntax is correct, proceeding to next step.");
                    let mut file = OpenOptions::new().write(true).append(true).open(file_path)?;
                    write!(file, "{}\n", code_section_temp)?;
                    break;
                }
                Err(err) => {
                    println!("Syntax error: {}. Retrying API request...", err);
                    let mut del_last_line_err = remove_last_line(&err);
                    //println!("{}", del_last_line_err.unwrap().as_str());
                    let request_body = ApiRequest {
                        model: "llama3".to_string(),
                        messages: vec![
                            Message { role: "user".to_string(), content: pre_command_text.to_string() },
                            Message { role: "assistant".to_string(), content: "ok, I will answer you DSL result".to_string() },
                            Message {
                                role: "user".to_string(),
                                content: format!("{}", input_text),
                            },
                            Message {
                                role: "assistant".to_string(),
                                content: format!("{}", code_section_temp),
                            },
                            Message {
                                role: "user".to_string(),
                                content: format!(r#"
The DSL you just generated is incorrect, Please regenerate: "{}"
only DSL. The wrong message is: {}
Please regenerate the DSL using these guidelines and correct the identified errors,and Add ";" at the end of the generated DSL,only DSL.


The DSL generated correctly before “--> loc” does not need to change,save it. “--> loc” in the wrong message indicates where the problems might have occurred. Please follow these steps to correct the following part DSL:

1. **Consistency Check**: Ensure that the corrected DSL maintains consistency with the natural language input and that no information is lost during conversion.

2. **Syntax Correction**: Verify that the DSL symbols meet the syntax requirements. Ensure the natural language is fully transformed into DSL, with correct scope and range.
   - Example: For "in myapp" , convert it to "(packagename "myapp")".
   - Example: For "that implement myapp", convert it to "(implementation "myapp")".
   - Example: For "that match myapp", convert it to "(match "myapp")".

3. **Keyword Verification**: Ensure that the generated keywords meet the grammar requirements and are within the provided range.
   - Example: If the natural language mentions "class", the corresponding DSL keyword should be "class".

4. **Detailed Error Review**: Address specific syntax errors mentioned in the DSL syntax check message. For instance, if the message indicates a expected thing, correct it accordingly.
   - Example of correction:  ensure the generated DSL includes the correct scope range or the correct (...), [...] matched.

Please regenerate the DSL using these guidelines and correct the identified errors,and Add ";" at the end of the generated DSL,only DSL.
"#, input_text, del_last_line_err.unwrap().as_str()),
                            },
                        ],
                        stream: false,
                    };

                    // println!("{:?}:{}", request_body,code_section);
                    let mut response = post_api_request(&request_body, client, api_url).await;
                    code_section_temp = extract_code_section(&response.unwrap().message.content).unwrap().parse().unwrap();
                    // code_section_temp = code_section.to_string();
                }
            }
            attempts += 1;
            if attempts >= MAX_RETRIES { break; }
        }
    }
    Ok(())
}


fn remove_last_line(error: &pest::error::Error<Rule>) -> Result<String, Box<dyn StdError>> {
    let err_string = format!("{}", error); // 将错误转换为字符串
    let mut lines: Vec<&str> = err_string.lines().collect();
    if !lines.is_empty() {
        lines.pop();
    }
    let new_err = lines.join("\n");
    Ok(new_err)
}

/**
Todo:写一个异步函数更加合理,当guarding.rs写完api.json后,interractWithqianwen会继续执行在末尾行加//的操作
 */
//可能的方法：大模型输出while循环 提升识别准确
fn extract_code_section(text: &str) -> Option<&str> {
    // 扩充处理逻辑
    Some(text)  //
}

fn process_nl_string(input: &str) -> String {
    let mut result = String::new();
    // 将首字母大写转换为小写
    if let Some(first_char) = input.chars().next() {
        result.push(first_char.to_lowercase().next().unwrap());
    }
    // 将剩余的字符追加到结果字符串中
    result.push_str(&input[1..]);
    // 进行特定单词的替换
    result = result
        .replace("aPI", "API")
        .replace(" entities ", " entity ")
        .replace(" in ", " packagename ")
        .replace(" from "," packagename ")
        .replace("be public", "")
        .replace("be private", "")
        .replace("be protected", "")
        .replace("be static", "")
        .replace("be final", "")
        .replace("be abstract", "")
        .replace("be activelynative", "")
        .replace("be extensive", "")
        .replace("be local", "")
        .replace("be inner", "")
        .replace("be interface", "")
        .replace("be field", "")
        .replace("be anonymous", "")
        .replace("be base", "")
        .replace("Private", "private");

    result
}

fn check_semantic(code_section_p0: &str, input_text_p1: &str) -> Result<(), Box<dyn StdError>> {
    let nl_keywords: HashMap<&str, usize> = extract_keywords_with_count(input_text_p1);
    let dsl_keywords: HashMap<&str, usize> = extract_keywords_with_count(code_section_p0);
    //println!("nl_keywords{:?}\n dsl_keywords{:?}", nl_keywords, dsl_keywords);

    // 计算缺失元素
    let missing = calculate_difference(&nl_keywords, &dsl_keywords);
    // 计算多余元素
    let extra = calculate_difference(&dsl_keywords, &nl_keywords);

    // 检查缺失和多余元素的数量
    let missing_count: usize = missing.values().sum();
    let extra_count: usize = extra.values().sum();

    // 确保缺失和多余元素都为空，且数量都为0
    if missing.is_empty() && extra.is_empty() && missing_count == 0 && extra_count == 0 {
        Ok(())
    } else {
        Err(Box::from(format!(
            "Semantic mismatch: Missing elements: {:?} , Extra elements: {:?}",
            missing.keys(), extra.keys()
        )))
    }
}

fn extract_keywords_with_count<'a>(text: &'a str) -> HashMap<&'a str, usize> {
    let keywords = vec![
        "public", "private", "protected", "static", "final", "abstract", "activelynative",
        "extensive", "local", "inner", "API", "interface", "field", "anonymous", "base",
        "transitiveDependency", "non-SDK-API", "promoted-through-intrusive-modify",
        "intrusive-modify", "reflect-modify",

        "packagename", "match","parameter","entity","class"
    ];
    let rule_level_keywords = vec![
        "package", "class", "struct", "function", "file", "method", "parameter", "codeBlock", "entity", "object", "variable",
    ];
    let mut keyword_map = HashMap::new();

    for keyword in keywords {
        let count = text.matches(keyword).count();
        keyword_map.insert(keyword, count);
    }

    keyword_map
}

fn calculate_difference<'a>(
    source: &'a HashMap<&'a str, usize>,
    target: &'a HashMap<&'a str, usize>,
) -> HashMap<&'a str, usize> {
    let mut difference = HashMap::new();

    for (key, &source_count) in source {
        let target_count = target.get(key).unwrap_or(&0);
        if source_count > *target_count {
            difference.insert(*key, source_count - target_count);
        }
    }

    difference
}

fn check_syntax(input: &str) -> Result<(), pest::error::Error<Rule>> {
    IdentParser::parse(Rule::start, input).map(|_| ())
}

#[tokio::main]
pub async fn llm_trans_with_llama(Precommand: &str, output: &str) {
    let mut input = String::new();
    println!("Please input architecture default detection rules in natural language：");
    match io::stdin().read_line(&mut input) {
        Ok(_) => {
            println!("\nInput：{}", input.trim());
        }
        Err(error) => {
            eprintln!("Error occurred while reading input: {}", error);
        }
    }
    let pre_command_file_path = Precommand;
    let client = Client::new();
    let pre_command = match fs::read_to_string(pre_command_file_path) {
        Ok(content) => content.trim().to_string(),  // 去除末尾换行符
        Err(error) => {
            eprintln!("Error occurred while reading instruction file: {}", error);
            return;
        }
    };
    if let Err(e) = call_api_and_write_to_file(pre_command.as_str(), input.trim(), &output, &client).await {
        eprintln!("Error: {}", e);
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use tokio;

    #[test]
    fn test() {
        llm_trans_with_llama("C:/LSDocument/GitHub/guarding/preCommand.txt", "C:/LSDocument/GitHub/guarding/guarding.guarding");
    }
}
