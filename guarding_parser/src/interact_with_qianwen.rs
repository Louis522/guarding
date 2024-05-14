use reqwest::Client;
use serde::{Deserialize, Serialize};
use toml::de;
use std::fs::OpenOptions;
use std::{fs, io};
use std::io::Write;
use tokio;

//配置
#[derive(Debug, Deserialize, Serialize)]
struct AppConfig {
    api_key: String,
    pre_command_file_path: String,
    guarding_file_path: String
}

#[derive(Serialize)]
struct ApiRequest {
    model: String,
    input: InputData,
    parameters: Parameters,
}

#[derive(Serialize)]
struct InputData {
    messages: Vec<Message>,
}

#[derive(Serialize)]
struct Message {
    role: String,
    content: String,
}

#[derive(Serialize)]
struct Parameters {
    result_format: String,
}

#[derive(Deserialize)]
struct ApiResponse {
    output: Output,
}

#[derive(Deserialize)]
struct Output {
    text: String,
    finish_reason: String,
}

fn load_config(file_path: &str) -> Result<AppConfig, Box<dyn std::error::Error>> {
    let contents = fs::read_to_string(file_path)?;
    Ok(de::from_str(&contents)?)
}

async fn call_api_and_write_to_file(api_key: &str, pre_command_text: &str,input_text:&str, file_path: &str,client: &Client) -> Result<(), Box<dyn std::error::Error>> {
    let api_url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    let request_body = ApiRequest {
        /**
        *更好的模型？
        */
        model: "qwen-max-longcontext".to_string(),
        input: InputData {
            messages: vec![
                Message { role: "system".to_string(), content: "You are a helpful assistant.".to_string() },
                Message { role: "user".to_string(), content: pre_command_text.to_string() },
                Message { role: "user".to_string(), content: input_text.to_string() },
            ],
        },
        parameters: Parameters {
            result_format: "text".to_string(),
        },
    };

    let response = client.post(api_url)
        .bearer_auth(api_key)
        .json(&request_body)
        .send()
        .await?
        .json::<ApiResponse>()
        .await?;

    if let Some(code_section) = extract_code_section(&response.output.text) {
        let mut file = OpenOptions::new().write(true).append(true).open(file_path)?;
        writeln!(file, "{}", code_section)?;
    }

    Ok(())
}
/**
 Todo:写一个异步函数更加合理,当guarding.rs写完api.json后,interractWithqianwen会继续执行在末尾行加//的操作
 */
//可能的方法：大模型输出while循环 提升识别准确
fn extract_code_section(text: &str) -> Option<&str> {
    // 扩充处理逻辑
    Some(text)  //
}

#[tokio::main]
pub async fn llm_trans_with_qianwen() {

    let config = load_config("config.toml").expect("Failed to load config");
    let  api= config.api_key;
    let mut input = String::new();
    let pre_command_file_path = config.pre_command_file_path;
    let  client = Client::new();

    /**
     *预处理
     */
    let pre_command = match fs::read_to_string(pre_command_file_path) {
        Ok(content) => content.trim().to_string(),  // 去除末尾换行符
        Err(error) => {
            eprintln!("读取指令文件时发生错误: {}", error);
            return;
        }
    };
    println!("请输入：");
    match io::stdin().read_line(&mut input){
        Ok(_) => {
            println!("输入的内容是：{}", input.trim());
        },
        Err(error) => {
            eprintln!("读取输入时发生错误: {}", error);
        }
    }
    if let Err(e) = call_api_and_write_to_file(&*api, pre_command.as_str(), input.trim(), config.guarding_file_path.as_str(), &client).await {
        eprintln!("Error: {}", e);
    }

}
/*#[cfg(test)]
mod tests {
    use super::*;
    use tokio;

    #[test]
    fn test() {
        llm_trans_with_qianwen("Service包里原生的，公共的类不应该被controller包里伴生的类实现");
    }
*/