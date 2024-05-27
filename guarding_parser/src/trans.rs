use std::convert::TryInto;
use std::ffi::c_int;
use std::fmt::{format, write};

extern crate regex;

use regex::Regex;
use heck::ToTitleCase;
use std::fmt::{Display, Formatter, Result};
use crate::ast::{Expr, GuardRule, Operator, RuleAssert, Attribute, RuleLevel, RuleType, RuleScope};

impl ToString for GuardRule {
    fn to_string(&self) -> String {
        let Opr = vec_operator_to_string(&self.ops, &self.assert);
        let mut result = format!("{}.that().{}{}.should().{}",
                                 self.level.to_string(),
                                 vec_attribute_to_string(&self.attr, &self.scope),
                                 //that
                                 self.scope.to_string(),
                                 //should
                                 // self.origin,
                                 // guard_rule.ty.to_string(),
                                 // guard_rule.expr.to_string(),
                                 //首字符是否为Not,
                                 // guard_rule.ops.to_sting(),
                                 //vec_operator_to_string(&self.ops, &self.assert),
                                 //近似scope处理
                                 Vec_assert_to_string(&self.assert, Opr, &self.scope));
        //self.assert.to_string());
        result
    }
}

fn Vec_assert_to_string(assert: &RuleAssert, opr: String, scope: &RuleScope) -> String {
    let mut result = String::new();
    {
        match assert {
            RuleAssert::Empty => {
                if let Some(app_slice) = opr.strip_suffix("That().") {
                    // 如果是以"That()."结尾，则替换为"()"
                    let modified_app = format!("{}()", app_slice);
                    result.push_str(&modified_app);
                } else {}
            }
            /**
            ToDo
             */
            RuleAssert::Stringed(scp, str) => {}
            RuleAssert::Leveled(lv, scp, package_ident) => {}
            RuleAssert::ArrayStringed(lv, attr, scp) => {
                result.push_str(&*opr);
                let mut attribute = vec_assert_attribute_to_string(attr);
                //app内容是.arePublic().should().等
                for (i, app) in attribute.iter().enumerate() {
                    /*// 检查app是否以"are"开头
                    if let Some(app_slice) = app.strip_prefix("are") {
                        // 如果是以"are"开头，则替换为"be"
                        let modified_app = format!("be{}", app_slice);
                        result.push_str(&modified_app);
                    } else {
                        // 如果不是以"are"开头，则直接追加原字符串
                        result.push_str(app);
                    }*/

                    result.push_str(app);

                    /**非not operator 省去追加的多个not~operaror~ruleLevel~.
                                        if(opr.starts_with("not")){
                                        result.push_str(&*opr);
                                    }*/
                    result.push_str(&*opr);
                }

                /**
                 assert的scope若为All,去除末尾.and().andShould().notImplementClassesThat().
                后续若有
                 */
                match scp {
                    RuleScope::All => {
                        //由于rust特性,无法对string使用pop
                        // 计算opr的长度并从result的末尾移除相应长度的字符串加上.should().一并移除
                        let opr_length = opr.len();
                        if (opr.starts_with("not")) {
                            //只去掉.should().
                            result.truncate(result.len() - (opr_length + 13));
                        } else {
                            result.truncate(result.len() - 13);
                        }
                    }
                    _ => {}
                }

                /**
                //在末尾resideIn插入之前要先检查末尾是不是implementclases等
                let opr_length = opr.len();
                if(result.ends_with(&*opr)){
                    result.truncate(result.len() -opr_length );
                }*/
                result.push_str(&*scp.to_string())
            }
            _ => {}
        }
    }
    result
}

fn trim_after_last_andshould(input: &str) -> String {
    // 创建一个正则表达式，匹配以"andShould()"开始直到字符串结束的部分
    let re = Regex::new(r"andShould\(\).+$").unwrap();
    // 使用replace_all方法移除匹配到的部分
    re.replace_all(input, "").into_owned()
}

/**
ToDO:带层级的Json输出

classes() that() defined

{
   "predicates": [
      "areExtensive",
      "areStatic",
      "arePublic",
     "resideInAPackage(Service)"
   ]
   "conditions": [
                  {    "pedicates": [
                               "arePrivate",
                               "areActivelyNative"
                        ]
                        "condition": [
                               "notImplementClassesThat()"
                               "resideInAPackage(controller)"

                        ]
                  }

                  {
                  "predicate": null,
                  "condition": "
                  }

                  {
                  "predicate": null,
                  "condition": "resideInAPackage(msmq)"
                  }
                 ]
}

class[extensive public static (packagename "Service", "msmq")] should not extend class[activelynative private (packagename "controller")]

classes().that().areExtensive().and().arePublic().and().areStatic().and().resideInAPackage(Service).and().resideInAPackage(msmq).  // resideInAnyPackage(Service,msmq)
should().notImplementClassesThat().areActivelyNative().
andShould().notImplementClassesThat().arePrivate().
andShould().notImplementClassesThat().resideInAPackage(controller) //
输出到指定文件,命令行输出 如output

{
   "predicates": [
      "areExtensive",
      "areStatic",
      "arePublic",
      {methodName:"residentInAnyPackage",
       args:["Service","msmq"]
       }
                                                //"resideInAPackage(Service,msmq)"//"resideInAnyPackage
   ]
   "conditions": [
                  {    "pedicates": [
                               "arePrivate",
                               "areActivelyNative"
                        ]
                        "condition": [
                               "notImplementClassesThat()"
                               "resideInAPackage(controller)"

                        ]
                  }
]
}
 */


impl ToString for RuleLevel {
    fn to_string(&self) -> String {
        match self {
            RuleLevel::Class => "classes()".to_string(),
            RuleLevel::Package => "package()".to_string(),
            RuleLevel::Function => "function()".to_string(),
            RuleLevel::Struct => "struct()".to_string(),
            RuleLevel::Method => "methods()".to_string(),
            RuleLevel::File => "files()".to_string(),
            RuleLevel::Parameter => "parameters()".to_string(),
            RuleLevel::CodeBlock => "codeblocks()".to_string(),
            RuleLevel::Entity => "entities()".to_string(),
            RuleLevel::Interface => "interfaces()".to_string(),
            RuleLevel::Object => "objects()".to_string(),
            RuleLevel::API => "methods()".to_string(),
            //ToDo ... 对其他级别的转换
        }
    }
}

fn vec_attribute_to_string(attr: &Vec<Attribute>, scope: &RuleScope) -> String {
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
                Attribute::ActivelyNative => "areActivelyNative().and().",
                Attribute::Extensive => "areExtensive().and().",
                Attribute::IntrusivelyNative => "areIntrusivelyNative().and().",
                Attribute::Local => "areLocal().and().",
                Attribute::Interface => "areInterfaces().and().",
                _ => "op.to_string().as_str()",
            })
        } else {
            break;
        }
    }
    //匹配是否scope为All,进行处理去除末尾.and().
    match scope {
        RuleScope::All => {
            result.drain(result.len() - 7..);
        }
        _ => {}
    }

    result
}

fn vec_assert_attribute_to_string(attr: &Vec<Attribute>) -> Vec<String> {
    //println!("{}",attr.len());
    let mut result = Vec::new();
    let mut iter = attr.iter();

    let mut index = 0;
    while true {
        if let Some(op) = iter.next() {
            index += 1;
            result.push(match op {
                Attribute::Public => "arePublic().andShould().",
                Attribute::Private => "arePrivate().andShould().",
                Attribute::Protected => "areProtected().andShould().",
                Attribute::Static => "areStatic().andShould().",
                Attribute::Final => "areFinal().andShould().",
                Attribute::Abstract => "areAbstract().andShould().",
                Attribute::ActivelyNative => "areActivelyNative().andShould().",
                Attribute::Extensive => "areExtensive().andShould().",
                Attribute::Local => "areLocal().andShould().",
                Attribute::Interface => "areInterfaces().andShould().",
                _ => "op.to_string().as_str()",
            }.to_string())
        } else {
            break;
        }
    }
    result
}

//首字母大写+去掉若干尾字符
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
            RuleScope::All => "".to_string(),
            RuleScope::PathDefine(path) => format!("PathDefine({})", path),
            RuleScope::Extend(extension) => format!("Extend({})", extension),
            RuleScope::Assignable(assignable) => format!("Assignable({})", assignable),
            RuleScope::Implementation(implementation) => format!("Implementation({})", implementation),
            RuleScope::MatchRegex(regex) => format!("MatchRegex({})", regex),
            RuleScope::ActivelyNative(path) => format!("areActivelyNative().andShould().{}", path_join(path)), //????????????????
            RuleScope::Extensive(path) => format!("areExtensive().andShould().{}", path_join(path)),
            RuleScope::PackageName(path) => format!("{}", path_join(path)),
            //...其他scope扩充/多scope处理
        }
    }
}

//对象路径处理
fn path_join(path: &Vec<String>) -> String {
    let mut result = String::new();

    if (path.len() == 1) {
        result.push_str("resideInAPackage(");
        result.push_str(path.first().unwrap().as_str());
    } else {
        result.push_str("resideInAnyPackage(");
        for (i, app) in path.iter().enumerate() {
            if i != path.len() {
                result.push_str(app);
                result.push_str(",");
            }
        }
        if result.ends_with(",") {
            result.pop();
        }
    }
    result.push_str(")");
    result
}

/**
*预期notBeExtendedByClassesThat().
 或者BeExtendedByClassesThat().
 */
fn vec_operator_to_string(ops: &Vec<Operator>, assert: &RuleAssert) -> String {
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
                            Operator::Embed =>"notEmbed",

                            Operator::BeAbstract => "notBeAbstract",
                            Operator::BeFinal => "notBeFinal",
                            Operator::BeStatic => "notBeStatic",
                            Operator::BePublic => "notBePublic",
                            Operator::BePrivate => "notBePrivate",
                            Operator::BeProtected => "notBeProtected",
                            Operator::BeActivelyNative => "notBeActivelyNative",
                            Operator::BeExtensive => "notBeExtensive",
                            Operator::BeIntrusivelyNative => "notBeIntrusivelyNative",
                            _ => &"not something",
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
                Operator::Embed => "Embed",

                Operator::BeAbstract => "BeAbstract",
                Operator::BeFinal => "BeFinal",
                Operator::BeStatic => "BeStatic",
                Operator::BePublic => "BePublic",
                Operator::BePrivate => "BePrivate",
                Operator::BeProtected => "BeProtected",
                Operator::BeActivelyNative => "BeActivelyNative",
                Operator::BeExtensive => "BeExtensive",
                Operator::BeIntrusivelyNative => "BeIntrusivelyNative",
                _ => "op.to_string().as_str()",
            }
                            /* *
                             Todo:非not情况直接输出 需要进行函数改造
                             */
            )
        } else {}
    }
    let level = to_string_without_brackets(assert.to_string(), 2);
    result.push_str(&level);
    result.push_str("That().");
    result
}


impl ToString for RuleAssert {
    fn to_string(&self) -> String {
        match self {
            RuleAssert::Empty => "".to_string(),
            RuleAssert::Stringed(RuleScope, String1) => self.to_string(),
            RuleAssert::Leveled(RuleLevel, RuleScope, String2) => {
                let str1 = to_string_without_brackets(RuleLevel.to_string(), 2);
                let str2 = to_string_without_brackets(RuleScope.to_string(), 1);
                format!("{}That().{}", str1, RuleScope.to_string())
            }
            RuleAssert::ArrayStringed(RuleLevel, Attribute, RuleScope) => {
                let str1 = to_string_without_brackets(RuleLevel.to_string(), 2);
                let str2 = vec_assert_attribute_to_string(Attribute);
                let str3 = to_string_without_brackets(RuleScope.to_string(), 1);
                /**
                format!("{}That().{}{}", str1, str2, RuleScope.to_string());
                format!("{}That().{}{}", str1, str2, RuleScope.to_string())
                 */
                format!("{}", str1)
            }
            //             str1           str2                             RoleScope.to_string()
            //notImplement Classes That().areActivelyNative(). andShould().resideInAPackage(controller)
            //                            arePublic(). andShould()
            //   notImplement Classes That()
            //添加: 在每一个andShould后加 notImplement Classes That()
            //   前段notImplement,   解决思路:operator_函数传入Assert的RuleScope,把class层级传入与前段拼接字符串.输出到result,该result传入到assert进行处理
            RuleAssert::Sized(usize) => "NotEmpty".to_string()
            // ... 对其他断言的转换
        }
    }
}



