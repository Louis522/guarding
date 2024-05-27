extern crate serde;

use std::path::PathBuf;
use std::io::prelude::*;
use serde_json::{json, Value};
use std::fs::File;
use guarding_core::domain::code_file::CodeFile;
use guarding_ident::ModelBuilder;
use guarding_core::rule_executor::{RuleErrorMsg, RuleExecutor};
use guarding_parser::ast::GuardRule;
use guarding_parser::parser;
use guarding_parser::interact_with_qianwen;

//,outfile: PathBuf
pub fn exec_guarding(rule_content: String, code_dir: PathBuf, output: PathBuf) -> Vec<RuleErrorMsg> {
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
            println!("{:?}", result_string);
            //result String写到json文件里
            let json_value = json!({ "result": result_string });
            let json_string = serde_json::to_string_pretty(&json_value).unwrap();
            match File::create(&output) {
                Ok(mut file) => match file.write_all(json_string.as_bytes()) {
                    Ok(_) => println!("Successfully wrote to {}", output.display()),
                    Err(e) => eprintln!("Error writing to file: {}", e),
                },
                Err(e) => eprintln!("Error creating file: {}", e),
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


#[cfg(test)]
mod tests;
