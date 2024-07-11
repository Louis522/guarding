use std::collections::HashMap;
use std::fmt;
use std::time::SystemTime;

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct ApiOutPutHistory {
    pub command: String,
    pub DSL: String,
    pub apiResult: Vec<String>,
    pub checktime: SystemTime,
    pub  status: Status,
}
#[derive(Clone, Debug, Eq, PartialEq)]
pub enum Status{
    Success,
    Failed
}

impl ApiOutPutHistory {
    pub fn new(command: String, dsl: String, api_result: Vec<String>, checktime: SystemTime, status: Status) -> Self {
        ApiOutPutHistory {
            command,
            DSL: dsl,
            apiResult: api_result,
            checktime,
            status,
        }
    }
}

impl fmt::Display for ApiOutPutHistory {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "Command: {}\nDSL: {}\nResults: {:?}\nChecked at: {:?}\nStatus: {:?}",
               self.command, self.DSL, self.apiResult, self.checktime, self.status)
    }
}

impl Status {
    /// 判断Status是否为Success
    pub fn is_success(&self) -> bool {
        matches!(self, Status::Success)
    }

    /// 判断Status是否为Failed
    pub fn is_failed(&self) -> bool {
        matches!(self, Status::Failed)
    }
}