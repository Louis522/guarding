use reqwest::Client;
use serde::{Deserialize, Serialize};
use toml::de;
use std::fs::OpenOptions;
use std::{fs, io};
use std::io::Write;
use std::path::PathBuf;
use tokio;

use crate::api_output_history;

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

async fn call_api_and_write_to_file(pre_command_text: &str, input_text: &str, file_path: &str, client: &Client) -> Result<(), Box<dyn std::error::Error>> {
    let api_url = "http://localhost:11434/api/chat";
    let request_body = ApiRequest {
        model: "llama3".to_string(),
        messages: vec![
            Message { role: "user".to_string(), content: pre_command_text.to_string() },
            Message { role: "assistant".to_string(), content: "ok, I will answer you DSL result".to_string() },
            Message {
                role: "user".to_string(),
                content: format!("{}(请仅输出DSL)", input_text),
            },
        ],
        stream: false,
    };
    println!("Gnerating Architecture DSL...");
    let response = client.post(api_url)
        .json(&request_body)
        .send()
        .await?
        .json::<ApiResponse>()
        .await?;
    if let Some(code_section) = extract_code_section(&response.message.content) {
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
pub async fn llm_trans_with_llama(Precommand: &str, output: &str) {
    let mut input = String::new();
    println!("请输入需检测架构的规则：");
    match io::stdin().read_line(&mut input) {
        Ok(_) => {
            println!("输入的内容是：{}", input.trim());
        }
        Err(error) => {
            eprintln!("读取输入时发生错误: {}", error);
        }
    }
    let pre_command_file_path = Precommand;
    let client = Client::new();
    let pre_command = match fs::read_to_string(pre_command_file_path) {
        Ok(content) => content.trim().to_string(),  // 去除末尾换行符
        Err(error) => {
            eprintln!("读取指令文件时发生错误: {}", error);
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
