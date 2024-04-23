extern crate serde;

use std::path::PathBuf;

use guarding_core::domain::code_file::CodeFile;
use guarding_ident::ModelBuilder;
use guarding_core::rule_executor::{RuleErrorMsg, RuleExecutor};
use guarding_parser::ast::GuardRule;
use guarding_parser::parser;
use guarding_parser::interact_with_qianwen;

pub fn exec_guarding(rule_content: String, code_dir: PathBuf) -> Vec<RuleErrorMsg> {
    /*使用guarding parser模块提供的parse方法解析rule_content变量的内容，然后针对解析结果进行模式匹配处理
      主要关注的部分
     */

    match parser::parse(rule_content.as_str()) {
        Err(e) => {
            println!("{}", e);
            vec![]
        },

        Ok(rules) => {
            for rule in rules {
                let result_string = rule.to_string();
                println!("{}", result_string)
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
