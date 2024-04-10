use std::convert::TryInto;
use std::ffi::c_int;
use std::fmt::write;
use heck::ToTitleCase;
use std::fmt::{Display, Formatter, Result};
use crate::ast::{Expr, GuardRule, Operator,RuleAssert,Attribute, RuleLevel, RuleType, RuleScope};

impl ToString for GuardRule {
    fn to_string(&self) -> String {
        let mut result = format!("{}.that().{}{}.should().{}{}",
               self.level.to_string(),
               vec_attribute_to_string(&self.attr),
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
            RuleLevel::Class => "classes()".to_string(),
            RuleLevel::Package => "package()".to_string(),
            RuleLevel::Function => "function()".to_string(),
            RuleLevel::Struct => "struct()".to_string(),
            // ... 对其他级别的转换
        }
    }
}

fn vec_attribute_to_string(attr: &Vec<Attribute>) -> String {
    //println!("{}",attr.len());
    let mut result = String::new();
    let mut iter = attr.iter();

    let mut index = 0;
    while true {
        if let Some(op) = iter.next() {
            index += 1;
            result.push_str(match op {
                Attribute::Public => "arePublic().and().",
                Attribute::Private => "arePrivate().and().",
                Attribute::Protected => "areProtected().and().",
                Attribute::Static => "areStatic().and().",
                Attribute::Final => "areFinal().and().",
                Attribute::Abstract => "areAbstract().and().",
                _ => "op.to_string().as_str()",
            })

        } else {
              break;
        }
    }

    result
}
fn to_string_without_brackets(s: String, s2: i8) -> String {
    let original_string = s.to_string().to_title_case();
    let len = original_string.len();
    if len >= 3 && &original_string[len - 3..] == "()."
    {
        original_string[..len - (s2 as usize)].to_string()
    } else {
        original_string.clone()
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
            RuleScope::ActivelyNative(path) => format!("areActivelyNative().andShould().resideInAPackage(\"{}\")", path), //????????????????
            RuleScope::Extensive(path) => format!("areExtensive().andShould().resideInAPackage(\"{}\")", path),
            RuleScope::PackageName(path) => format!("resideInAPackage(\"{}\")", path),
            //...其他scope扩充/多scope处理
        }
    }
}


fn vec_operator_to_string(ops: &Vec<Operator>) -> String {
    //println!("{}",ops.len());
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
                            Operator::AccessBy => "notBeAccessedBy",
                            Operator::DependBy => "notBeDependBy",
                            Operator::Extend => "notExtend",
                            Operator::ExtendBy => "notBeExtendedBy",
                            Operator::Implement => "notImplement",
                            Operator::FreeOfCircle => "notFreeOfCircle",
                            _ => &  "not something",
                        }
                    } else {
                        panic!("Invalid operator sequence: 'Not' must be followed by another operator");
                    }
                }
                Operator::AccessBy => "BeAccessedBy",
                Operator::DependBy => "BeDependedBy",
                Operator::Extend => "Extend",
                Operator::ExtendBy => "BeExtendedBy",
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
            RuleAssert::Leveled(RuleLevel,RuleScope,String)=> {
                let str1= to_string_without_brackets(RuleLevel.to_string(),2);
                let str2=to_string_without_brackets(RuleScope.to_string(),1);
                format!("{}That().{}",str1,RuleScope.to_string())
            },
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

fn remove_last_two_chars(s: &str) -> &str {
    let mut chars = s.chars();
    if chars.by_ref().count() >= 3 {
        chars.nth_back(3);
    }
    chars.as_str()
}



