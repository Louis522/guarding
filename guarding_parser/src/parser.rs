use pest::iterators::{Pair, Pairs};
use pest::Parser;

use crate::errors::{Error, Result as GuardingResult};
use crate::ast::{Expr, GuardRule, Operator, Attribute, RuleAssert, RuleLevel, RuleScope, RulePriority};
use crate::parser::Rule::scope;
use crate::support::str_support;

#[derive(Parser)]
#[grammar = "guarding.pest"]
struct IdentParser;

pub fn parse(code: &str) -> GuardingResult<Vec<GuardRule>> {
    match IdentParser::parse(Rule::start, code) {
        Err(e) => {
            let fancy_e = e.renamed_rules(|rule| {
                match *rule {
                    Rule::operator => {
                        format!("{:?}", rule)
                    }
                    _ => {
                        format!("{:?}", rule)
                    }
                }
            });
            return Err(Error::msg(fancy_e));
        }
        Ok(pairs) => {
            Ok(consume_rules_with_spans(pairs))
        }
    }
}

//对规则文本进行处理
fn consume_rules_with_spans(pairs: Pairs<Rule>) -> Vec<GuardRule> {
    pairs.filter(|pair| {

        //通过.filter()方法遍历输入的pairs，仅保留那些 .as_rule() 方法返回值为 Rule::declaration 的配对
        return pair.as_rule() == Rule::declaration;
    }).map(|pair| {
        let mut rule: GuardRule = Default::default();
        for p in pair.into_inner() {
            match p.as_rule() {
                Rule::normal_rule => {
                    rule = parse_normal_rule(p);
                }
                Rule::layer_rule => {
                    rule = parse_layer_rule(p);
                }
                _ => panic!("unreachable content rule: {:?}", p.as_rule())
            };
        }

        return rule;
    })
        .collect::<Vec<GuardRule>>()
}

fn parse_layer_rule(_pair: Pair<Rule>) -> GuardRule {
    println!("todo: in processed");
    GuardRule::default()
}

fn parse_normal_rule(pair: Pair<Rule>) -> GuardRule {
    let mut guard_rule = GuardRule::default();

    for p in pair.into_inner() {
        match p.as_rule() {
            Rule::rule_level => {
                guard_rule.level = parse_rule_level(p);
            }
            Rule::attribute => {
                guard_rule.attr = parse_attr(p);
            }
            Rule::use_symbol => {
                // may be can do something, but still nothing.
            }
            Rule::expression => {
                guard_rule.expr = parse_expr(p);
            }
            Rule::operator => {
                guard_rule.ops = parse_operator(p);
            }
            Rule::assert => {
                guard_rule.assert = parse_assert(p);
            }
            Rule::scope => {
                guard_rule.scope = parse_scope(p);
            }
            Rule::priority => {
                guard_rule.priority = parse_priority(p);
            }
            Rule::should => {
                // should do nothing
            }
            Rule::only => {
                // should do nothing
            }
            _ => {
                println!("implementing rule: {:?}, level: {:?}", p.as_rule(), p.as_span());
            }
        }
    }

    guard_rule
}

fn parse_rule_level(pair: Pair<Rule>) -> RuleLevel {
    let level_str = pair.as_span().as_str();
    match level_str {
        "package" => { RuleLevel::Package }
        "function" => { RuleLevel::Function }
        "class" => { RuleLevel::Class }
        "struct" => { RuleLevel::Struct }
        "method" => { RuleLevel::Method }
        "parameter" => { RuleLevel::Parameter }
        "file" => { RuleLevel::File }
        "codeBlock" => { RuleLevel::CodeBlock }
        "entity" => { RuleLevel::Entity }
        "interface" => { RuleLevel::Interface }
        "object" => { RuleLevel::Object }
        "API" => { RuleLevel::API }
        "variable" => { RuleLevel::Variable }
        &_ => { unreachable!("error rule level: {:?}", level_str) }
    }
}

fn parse_attr(parent: Pair<Rule>) -> Vec<Attribute> {
    let mut pairs = parent.into_inner();
    //let mut pair = pairs.next().unwrap();
    //Todo add SomeThing
    let mut attributes: Vec<Attribute> = vec![];
    for pair in pairs {
        // pair = p.into_inner().next().unwrap();
        let attribute = match pair.as_str() {
            "public" => { Attribute::Public }
            "private" => { Attribute::Private }
            "protected" => { Attribute::Protected }
            "static" => { Attribute::Static }
            "final" => { Attribute::Final }
            "abstract" => { Attribute::Abstract }
            "activelynative" => { Attribute::ActivelyNative }
            "intrusivelynative" => { Attribute::IntrusivelyNative }
            "extensive" => { Attribute::Extensive }
            "local" => { Attribute::Local }
            "interface" => { Attribute::Interface }
            "inner" => { Attribute::Inner }
            "field" => { Attribute::Field }
            "anonymous" => { Attribute::Anonymous }
            "non-SDK" => { Attribute::NonSDK }
            "API" => { Attribute::API }
            "intrusive-modify" => { Attribute::IntrusiveModify }
            "base" => { Attribute::Base }
            "transitiveDependency" => { Attribute::TransitiveDependency }
            "parameter" => { Attribute::Parameter }
            "non-SDK-API" => { Attribute::NonSDKAPI }
            "promoted-through-intrusive-modify" => { Attribute::PromotedThroughIntrusiveModify }
            "reflect-modify" => { Attribute::ReflectModify }
            &_ => {
                panic!()
            }
            _ => {
                panic!("implementing ops: {:?}, text: {:?}", pair.as_rule(), pair.as_span())
            }
        };
        attributes.push(attribute)
    }
    attributes
}


fn parse_operator(parent: Pair<Rule>) -> Vec<Operator> {
    let mut pairs = parent.into_inner(); //父级元素转换成子级元素
    let mut pair = pairs.next().unwrap(); //获取第一个子级元素,unwrap()强制获取
    let mut operators: Vec<Operator> = vec![];

    match pair.as_rule() {
        Rule::op_not | Rule::op_not_symbol => {
            operators.push(Operator::Not);
            // get next operator
            pair = pairs.next().unwrap().into_inner().next().unwrap();
        }
        _ => {}
    }

    let ops = match pair.as_rule() {
        Rule::op_lte => { Operator::Lte }
        Rule::op_gte => { Operator::Gte }
        Rule::op_lt => { Operator::Lt }
        Rule::op_gt => { Operator::Gt }
        Rule::op_eq => { Operator::Eq }
        Rule::op_ineq => { Operator::Ineq }

        Rule::op_contains => { Operator::Contains }
        Rule::op_endsWith => { Operator::Endswith }
        Rule::op_startsWith => { Operator::StartsWith }

        Rule::op_inside => { Operator::Inside }
        Rule::op_resideIn => { Operator::ResideIn }
        Rule::op_accessBy => { Operator::AccessBy }
        Rule::op_dependBy => { Operator::DependBy }
        Rule::op_extend => { Operator::Extend }
        //Rule::op_extendBy => { Operator::ExtendBy }
        Rule::op_implement => { Operator::Implement }
        Rule::op_freeOfCircle => { Operator::FreeOfCircle }
        Rule::op_embed => { Operator::Embed }
        Rule::op_rewrite => { Operator::Rewrite }
        Rule::op_inherit => { Operator::Inherit }
        Rule::op_dependOn => { Operator::DependOn }
        Rule::op_call =>{ Operator::Call}
        Rule::op_use => { Operator::Use }
        Rule::op_override => { Operator::Override }
        Rule::op_aggregate => { Operator::Aggregate }

        Rule::op_BePublic => { Operator::BePublic }
        Rule::op_BePrivate => { Operator::BePrivate }
        Rule::op_BeProtected => { Operator::BeProtected }
        Rule::op_BeAbstract => { Operator::BeAbstract }
        Rule::op_BeActivelyNative => { Operator::BeActivelyNative }
        Rule::op_BeStatic => { Operator::BeStatic }
        Rule::op_BeFinal => { Operator::BeFinal }
        Rule::op_BeExtensive => { Operator::BeExtensive }
        Rule::op_BeIntrusivelyNative => { Operator::BeIntrusivelyNative }
        _ => {
            panic!("implementing ops: {:?}, text: {:?}", pair.as_rule(), pair.as_span())
        }
    };

    operators.push(ops);

    operators
}

fn parse_expr(parent: Pair<Rule>) -> Expr {
    let mut pairs = parent.into_inner();
    let pair = pairs.next().unwrap();

    match pair.as_rule() {
        Rule::fn_call => {
            let mut call_chains: Vec<String> = vec![];

            for p in pair.into_inner() {
                match p.as_rule() {
                    Rule::identifier => {
                        let ident = p.as_span().as_str().to_string();
                        call_chains.push(ident);
                    }
                    _ => {}
                };
            };

            return Expr::PropsCall(call_chains);
        }
        _ => {
            panic!("implementing expr: {:?}, text: {:?}", pair.as_rule(), pair.as_span())
        }
    };
}

//修改加入对应的scope处理
fn parse_assert(parent: Pair<Rule>) -> RuleAssert {
    let mut pairs = parent.into_inner();
    let pair = pairs.next().unwrap();

    match pair.as_rule() {
        Rule::leveled => {
            let mut level = RuleLevel::Class;
            let mut str = "".to_string();
            let mut scp = RuleScope::All;
            for p in pair.into_inner() {
                match p.as_rule() {
                    Rule::rule_level => {
                        level = parse_rule_level(p);
                    }
                    Rule::string => {
                        str = str_support::replace_string_markers(p.as_str());
                    }
                    Rule::scope => {
                        scp = parse_scope(p);
                    }
                    _ => {}
                }
            }

            RuleAssert::Leveled(level, scp, str)
        }
        Rule::sized => {
            let mut pairs = pair.into_inner();
            let pair = pairs.next().unwrap();
            let size: usize = pair.as_str()
                .parse()
                .expect("convert int error");

            RuleAssert::Sized(size)
        }
        Rule::stringed => {
            let mut pairs = pair.into_inner();
            let mut scp = RuleScope::All;

            let pair1 = pairs.next().unwrap();
            scp = parse_scope(pair1);

            let pair2 = pairs.next().unwrap();
            let str = str_support::replace_string_markers(pair2.as_str());

            RuleAssert::Stringed(scp, str.to_string())
        }
        Rule::array_stringed => {
            //let mut array = vec![];
            let mut scp = RuleScope::All;
            let mut level = RuleLevel::Class;
            let mut attributes: Vec<Attribute> = vec![];
            for p in pair.into_inner() {
                match p.as_rule() {
                    Rule::rule_level => {
                        level = parse_rule_level(p);
                    }

                    Rule::attribute => {
                        attributes = parse_attr(p);
                        ;
                    }
                    Rule::scope => {
                        scp = parse_scope(p);
                    }
                    _ => {}
                }
            }

            RuleAssert::ArrayStringed(level, attributes, scp)
        }
        _ => { RuleAssert::Empty }
    }
}

fn parse_scope(parent: Pair<Rule>) -> RuleScope {
    let mut pairs = parent.into_inner();
    let pair = pairs.next().unwrap();

    match pair.as_rule() {
        Rule::path_scope => {
            let without_markers = str_support::replace_string_markers(pair.as_str());
            let string = str_support::unescape(without_markers.as_str()).expect("incorrect string literal");
            RuleScope::PathDefine(string)
        }
        /*Rule::assignable_scope => {
            let string = string_from_pair(pair);
            RuleScope::Assignable(string)
        }
        Rule::extend_scope => {
            let string = string_from_pair(pair);
            RuleScope::Extend(string)
        }
        Rule::match_scope => {
            let string = string_from_pair(pair);
            RuleScope::MatchRegex(string)
        }
        Rule::impl_scope => {
            let string = string_from_pair(pair);
            RuleScope::Implementation(string)
        }*/
        Rule::actively_native_scope => {
            let string = string_from_pair(pair);
            RuleScope::ActivelyNative(string)
        }
        Rule::extensive_scope => {
            let string = string_from_pair(pair);
            RuleScope::Extensive(string)
        }
        Rule::package_name_scope => {
            let string = string_from_pair(pair);
            RuleScope::PackageName(string)
        }
        _ => {
            println!("implementing scope: {:?}, text: {:?}", pair.as_rule(), pair.as_span());
            RuleScope::All
        }
    }
}

fn parse_priority(pair: Pair<Rule>) -> RulePriority {
    let level_str = pair.as_span().as_str();
    match level_str {
        "" => { RulePriority::Default }
        "HIGH" => { RulePriority::High }
        "MEDIUM" => { RulePriority::Medium }
        "LOW" => { RulePriority::Low }
        &_ => { unreachable!("error rule level: {:?}", level_str) }
    }
}

fn string_from_pair(pair: Pair<Rule>) -> Vec<String> {
    // let mut string = "".to_string();
    let mut result = vec![];
    for p in pair.into_inner() {
        match p.as_rule() {
            Rule::extent => {
                for inner in p.into_inner() {
                    match inner.as_rule() {
                        Rule::string => {
                            let without_markers = str_support::replace_string_markers(inner.as_str());
                            let string = str_support::unescape(without_markers.as_str()).expect("incorrect string literal");
                            result.push(string);
                        }
                        _ => {}
                    }
                }
            }
            Rule::string => {
                let without_markers = str_support::replace_string_markers(p.as_str());
                let string = str_support::unescape(without_markers.as_str()).expect("incorrect string literal");
                result.push(string);
            }
            _ => {}
        }
    }
    result
}

#[cfg(test)]
mod tests {
    use crate::ast::{Expr, Operator, RuleAssert, RuleLevel, RuleScope};
    use crate::parser::parse;

    #[test]
    fn should_parse_string_assert() {
        let code = "class::name contains \"Controller\";";
        let rules = parse(code).unwrap();

        assert_eq!(1, rules.len());
        assert_eq!(RuleLevel::Class, rules[0].level);
        assert_eq!(RuleScope::All, rules[0].scope);
        // assert_eq!(RuleAssert::Stringed("Controller".to_string()), rules[0].assert);
    }

    #[test]
    fn should_parse_struct() {
        let code = "struct::name contains \"Controller\";";
        let rules = parse(code).unwrap();

        assert_eq!(RuleLevel::Struct, rules[0].level);
        //  assert_eq!(RuleAssert::Stringed("Controller".to_string()), rules[0].assert);
    }

    #[test]
    fn should_parse_package_asset() {
        let code = "class(\"..myapp..\")::function.name should contains(\"\");";
        let rules = parse(code).unwrap();

        assert_eq!(RuleScope::PathDefine(("..myapp..").to_string()), rules[0].scope);
        let chains = vec!["function".to_string(), "name".to_string()];
        assert_eq!(Expr::PropsCall(chains), rules[0].expr);
    }

    #[test]
    fn should_parse_package_extends() {
        let code = "class(extends \"Connection.class\")::name endsWith \"Connection\";";
        let vec = parse(code).unwrap();
        assert_eq!(1, vec[0].ops.len());
        assert_eq!(Operator::Endswith, vec[0].ops[0])
    }

    #[test]
    fn should_parse_not_symbol() {
        let code = "class(extends \"Connection.class\")::name should not endsWith \"Connection\";";
        let vec = parse(code).unwrap();
        assert_eq!(2, vec[0].ops.len());
        assert_eq!(Operator::Not, vec[0].ops[0]);
        assert_eq!(Operator::Endswith, vec[0].ops[1]);
        assert_eq!(RuleScope::Extend("Connection.class".to_string()), vec[0].scope);
    }

    #[test]
    fn should_parse_sized_assert() {
        let code = "class(\"..myapp..\")::function.vars.len should <= 20;";
        let vec = parse(code).unwrap();
        assert_eq!(RuleAssert::Sized(20), vec[0].assert);
    }

    #[test]
    fn should_parse_package_container_scope() {
        let code = "class(assignable \"EntityManager.class\") resideIn package(\"..persistence.\");";
        let vec = parse(code).unwrap();
        // assert_eq!(RuleAssert::Leveled(RuleLevel::Package, "..persistence.".to_string()), vec[0].assert);
    }

    #[test]
    fn should_parse_package_regex() {
        let code = "package(match(\"^/app\")) endsWith \"Connection\";";
        let vec = parse(code).unwrap();
        assert_eq!(RuleScope::MatchRegex("^/app".to_string()), vec[0].scope);
    }

    #[test]
    fn should_parse_array_stringed() {
        let code = "class(\"..service..\") only accessed([\"..controller..\", \"..service..\"]);";
        let vec = parse(code).unwrap();

        let results = vec!["..controller..".to_string(), "..service..".to_string()];
        //  assert_eq!(RuleAssert::ArrayStringed(results), vec[0].assert);
    }

    #[test]
    fn should_parse_class_compare() {
        let code = "class(\"..myapp..\")::function.name should not contains(\"\");
class(\"..myapp..\")::function.name !contains(\"\");

class(\"..myapp..\")::vars.len should <= 20;
class(\"..myapp..\")::function.vars.len should <= 20;
";
        parse(code).unwrap();
    }

    #[test]
    fn should_parse_simple_usage() {
        let code = "class::name.len should < 20;
function::name.len should < 30;
";
        parse(code).unwrap();
    }

    #[test]
    fn should_parse_arrow_usage() {
        let code = "class -> name.len should < 20;
function -> name.len should < 30;
";
        parse(code).unwrap();
    }

    #[test]
    fn should_parse_layer() {
        let code = "layer(\"onion\")
    ::domainModel(\"\")
    ::domainService(\"\")
    ::applicationService(\"\")
    ::adapter(\"com.phodal.com\", \"zero\");

";
        parse(code).unwrap();
    }

    #[test]
    fn should_ignore_error() {
        let content = "class(\"java.util.Map\") only something([\"com.phodal.pepper.refactor.staticclass\"]);";
        match parse(content) {
            Ok(_) => {
                assert!(false);
            }
            Err(err) => {
                let string = format!("{:?}", err);
                assert!(string.contains("^---"));
            }
        }
    }

    #[test]
    fn should_ignore_comments() {
        let code = "// path: src/*
";

        parse(code).unwrap();
    }
}