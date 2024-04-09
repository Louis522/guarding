use std::convert::TryInto;
use std::fmt::write;
use std::fmt::{Display, Formatter, Result};
use crate::ast::{Expr, GuardRule, Operator,RuleAssert, RuleLevel, RuleType, RuleScope};

impl ToString for GuardRule {
    fn to_string(&self) -> String {
        let mut result = format!("{}that().{}.should().{}{}",
               self.level.to_string(),
               //that
               //多个scope组合
               self.scope.to_string(),
               //should
               // self.origin,

               // guard_rule.ty.to_string(),
               // guard_rule.expr.to_string(),
               //首字符是否为Not,
               // guard_rule.ops.to_sting(),
               vec_operator_to_string(&self.ops),
               //近似scope处理
               self.assert.to_string());
        result
    }
}

impl ToString for RuleLevel {
    fn to_string(&self) -> String {
        match self {
            RuleLevel::Class => "classes().".to_string(),
            RuleLevel::Package => "package().".to_string(),
            RuleLevel::Function => "function().".to_string(),
            RuleLevel::Struct => "struct().".to_string(),
            // ... 对其他级别的转换
        }
    }
}

impl ToString for Operator {
    fn to_string(&self) -> String {
        match self {
            Operator::Gt => ">".to_string(),
            Operator::Gte => ">=".to_string(),
            Operator::Lt => "<".to_string(),
            Operator::Lte => "<=".to_string(),
            Operator::Eq => "==".to_string(),
            Operator::Ineq => "!=".to_string(),
            Operator::And => "and".to_string(),
            Operator::Or => "or".to_string(),
            Operator::Not => "!".to_string(),
            Operator::StartsWith => "startsWith".to_string(),
            _ => "".to_string(),
        }
    }
}
impl ToString for RuleScope {
    fn to_string(&self) -> String {
        match self {
            RuleScope::All => "All".to_string(),
            RuleScope::PathDefine(path) => format!("PathDefine({})", path),
            RuleScope::Extend(extension) => format!("Extend({})", extension),
            RuleScope::Assignable(assignable) => format!("Assignable({})", assignable),
            RuleScope::Implementation(implementation) => format!("Implementation({})", implementation),
            RuleScope::MatchRegex(regex) => format!("MatchRegex({})", regex),
            RuleScope::ActivelyNative(path) => format!("areActivelyNative().and().resideInAPackage(\"{}\")", path), //????????????????
            RuleScope::Extensive(path) => format!("areExtensive().and().resideInAPackage(\"{}\").", path),
            //...其他scope扩充/多scope处理
        }
    }
}


fn vec_operator_to_string(ops: &Vec<Operator>) -> String {
    println!("{}",ops.len());
    let mut result = String::new();
    let mut iter = ops.iter();


    let mut index = 0;
    while (index < iter.len()) {
        if let Some(op) = iter.next() {
            index += 1;
            result.push_str(match op {
                Operator::Not => {
                    index += 1;
                    if let Some(next_op) = iter.next() {
                        match next_op {
                            Operator::Accessed => "notAccessed",
                            Operator::DependBy => "notDependBy",
                            Operator::Extend => "notbeExtendedBy",
                            Operator::Implement => "notImplement",
                            Operator::FreeOfCircle => "notFreeOfCircle",
                            _ => &  "not something",
                        }
                    } else {
                        panic!("Invalid operator sequence: 'Not' must be followed by another operator");
                    }
                }
                Operator::Accessed => "Accessed",
                Operator::DependBy => "DependBy",
                Operator::Extend => "beExtendedBy",
                Operator::Implement => "Implement",
                Operator::FreeOfCircle => "FreeOfCircle",
                _ => "op.to_string().as_str()",
        })

        } else {

        }
    }

    result
}


impl ToString for RuleAssert {
    fn to_string(&self) -> String {
        match self {
            RuleAssert::Empty => "".to_string(),
            RuleAssert::Stringed(RuleScope,String) => self.to_string(),
            RuleAssert::Leveled(RuleLevel,RuleScope,String)=> format!("{}that().{}",RuleLevel.to_string(),RuleScope.to_string()),
                /**
                {
                match self{
                    RuleAssert::Leveled(RuleLevel::Class,RuleScope::Extensive(path),_)=> format!("{}().That().Extensive().definedIn({})",RuleLevel,path),

                    _ => {}
                }

            } .to_string(),
*/
            RuleAssert::ArrayStringed(RuleScope,String)=> "NotEmpty".to_string(),
            RuleAssert::Sized(usize)=> "NotEmpty".to_string()
            // ... 对其他断言的转换
        }
    }
}



