extern crate serde;

use std::path::PathBuf;
use std::io::{prelude, Seek, Write};
use serde_json::{json, Value};
use std::fs::{File, self, OpenOptions};
use guarding_core::domain::code_file::CodeFile;
use guarding_ident::ModelBuilder;
use guarding_core::rule_executor::{RuleErrorMsg, RuleExecutor};
use guarding_parser::ast::GuardRule;
use guarding_parser::parser;
use guarding_parser::interact_with_qianwen;

//,outfile: PathBuf
pub fn exec_guarding(rule_content: String, code_dir: PathBuf, output: PathBuf, input: &PathBuf) -> Vec<RuleErrorMsg> {
    /*使用guarding parser模块提供的parse方法解析rule_content变量的内容，然后针对解析结果进行模式匹配处理
      主要关注的部分
     */
    match parser::parse(rule_content.as_str()) {
        Err(e) => {
            println!("{}", e);
            vec![]
        }

        Ok(rules) => {
            let mut result_string = vec![];
            for rule in rules {
                result_string.push(rule.to_string());
            }
            println!("DSL: {:?}", rule_content.as_str());
            println!("API: {:?}", result_string);
            //result String写到json文件里
            let json_value = json!({"DSL": rule_content.as_str(),"result": result_string });
            let json_string = serde_json::to_string_pretty(&json_value).unwrap();
            match File::create(&output) {
                Ok(mut file) => match file.write_all(json_string.as_bytes()) {
                    Ok(_) => println!("Successfully wrote to {}", output.display()),
                    Err(e) => eprintln!("Error writing to file: {}", e),
                },
                Err(e) => eprintln!("Error creating file: {}", e),
            }
            // 清空guarding.guarding
            let file_to_clear_path = input.to_str().unwrap();
            match clear_file_contents(file_to_clear_path) {
                Ok(_) => {} ,
                Err(e) => eprintln!("在尝试清空 {} 文件内容时出错: {}", file_to_clear_path, e),
            }
            vec![]
            // let models = ModelBuilder::build_models_by_dir(code_dir);
            // exec(rules, models)
        }
    }
}

fn exec(rules: Vec<GuardRule>, models: Vec<CodeFile>) -> Vec<RuleErrorMsg> {
    let mut executor = RuleExecutor::new(models, rules);
    executor.run();

    return executor.errors;
}

fn clear_file_contents(file_path: &str) -> std::io::Result<()> {
    let mut file = OpenOptions::new()
        .write(true)
        .truncate(true)
        .open(file_path)?;

    // truncate(true)已经清空了文件
    Ok(())
}

#[cfg(test)]
mod tests;
