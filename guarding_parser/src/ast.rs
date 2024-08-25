use std::collections::HashMap;

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct GuardRule {
    pub origin: String,
    pub ty: RuleType,
    pub attr: Vec<Attribute>,
    pub level: RuleLevel,
    pub scope: RuleScope,
    pub expr: Expr,
    pub ops: Vec<Operator>,
    pub assert: RuleAssert,
    pub priority:RulePriority
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub enum LayeredRule {
    Normal(NormalLayered),
    Onion(OnionArch),
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct NormalLayered {}

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct OnionArch {}


impl Default for GuardRule {
    fn default() -> Self {
        GuardRule {
            origin: "".to_string(),
            ty: RuleType::Normal,
            attr: vec![],
            level: RuleLevel::Class,
            scope: RuleScope::All,
            expr: Expr::Identifier("".to_string()),
            ops: vec![],
            assert: RuleAssert::Empty,
            priority:RulePriority::Default,
        }
    }
}

impl GuardRule {
    pub fn assert_sized(rule: &GuardRule) -> usize {
        let mut size = 0;
        match &rule.assert {
            RuleAssert::Sized(sized) => {
                size = *sized;
            }
            _ => {}
        }
        size
    }

    pub fn assert_string(rule: &GuardRule) -> String {
        let mut string = "".to_string();
        match &rule.assert {
            RuleAssert::Stringed(scp, str) => {
                string = str.clone();
            }
            _ => {}
        }
        string
    }

    pub fn package_level(rule: &GuardRule) -> (bool, RuleLevel, String) {
        let mut string = "".to_string();
        let mut level = RuleLevel::Package;
        let mut has_capture = false;
        match &rule.assert {
            RuleAssert::Leveled(lv, scp, package_ident) => {
                has_capture = true;
                level = lv.clone();
                string = package_ident.clone();
            }
            _ => {}
        }

        return (has_capture, level, string);
    }
}

#[derive(Clone, Copy, Debug, Eq, PartialEq)]
pub enum RuleType {
    Normal,
    Layer,
}

#[derive(Clone, Copy, Debug, Eq, PartialEq)]
pub enum RuleLevel {
    Package,
    Function,
    Class,
    Struct,
    Parameter,
    File,
    Method,
    Entity,
    Interface,
    CodeBlock,
    Object,
    API,
    Variable,
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub enum RuleScope {
    All,
    PathDefine(String),
    Extend(Vec<String>),
    Assignable(Vec<String>),
    Implementation(Vec<String>),
    MatchRegex(Vec<String>),
    ActivelyNative(Vec<String>),
    Extensive(Vec<String>),
    PackageName(Vec<String>),
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub enum Expr {
    PropsCall(Vec<String>),
    Identifier(String),
}

/// A function call, can be a filter or a global function
#[derive(Clone, Debug, Eq, PartialEq)]
pub struct FunctionCall {
    /// The name of the function
    pub name: String,
    /// The args of the function: key -> value
    pub args: HashMap<String, Expr>,
}

impl FunctionCall {
    pub fn new(name: String) -> FunctionCall {
        FunctionCall {
            name,
            args: Default::default(),
        }
    }
}

impl Default for FunctionCall {
    fn default() -> Self {
        FunctionCall {
            name: "".to_string(),
            args: Default::default(),
        }
    }
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub enum Attribute {
    Public,
    Private,
    Protected,
    Static,
    Final,
    Abstract,
    ActivelyNative,
    Extensive,
    IntrusivelyNative,
    Local,
    Interface,

    Inner,
    Field,
    Anonymous,
    NonSDK,
    API,
    IntrusiveModify,
    Base,
    TransitiveDependency,
    Parameter,
    NonSDKAPI,
    PromotedThroughIntrusiveModify,
    ReflectModify
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub enum Operator {
    /// >
    Gt,
    /// >=
    Gte,
    /// <
    Lt,
    /// <=
    Lte,
    /// ==
    Eq,
    /// !=
    Ineq,
    /// and
    And,
    /// or
    Or,
    /// !
    /// not
    Not,

    // string assert operator
    StartsWith,
    Endswith,
    Contains,

    // package operators
    Inside,
    ResideIn,
    beAccessedBy,
    DependBy,
    Extend,
    ExtendBy,
    Implement,
    FreeOfCircular,
    Embed,
    Rewrite,
    Inherit,
    DependOn,
    Call,
    Use,
    Override,
    Aggregate,

    BePublic,
    BePrivate,
    BeProtected,
    BeStatic,
    BeFinal,
    BeAbstract,
    BeActivelyNative,
    BeExtensive,
    BeIntrusivelyNative,
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub enum RuleAssert {
    Empty,
    Stringed(RuleScope, String),
    Leveled(RuleLevel, RuleScope, String),
    ArrayStringed(RuleLevel, Vec<Attribute>, RuleScope),
    Sized(usize),
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub enum RulePriority {
    Low,
    Medium,
    High,
    Default
}