use std::fs;
use std::path::PathBuf;
use tokio;
use clap::{AppSettings, Clap};
use guarding::exec_guarding;
use guarding_parser::interact_with_qianwen;

#[derive(Clap)]
#[clap(version = "1.0", author = "Inherd Group <group@inherd.org>")]
#[clap(setting = AppSettings::ColoredHelp)]
struct Opts {
    #[clap(short, long ="input", default_value = "guarding.guarding")]
    input: String,
    #[clap(short, long, default_value = "src")]
    path: String,

    #[clap(short, long ="config", default_value = "config.toml")]
    config: String,
   // #[clap(short, long, default_value = "guard.json")]
   // output: String,
    #[clap(short, long, default_value = "api.json")]
    output: String,

    #[clap( long="llmCommand", default_value = "preCommand.txt")]
    command: String,
}

fn main() {
    let opts: Opts = Opts::parse();

    let buf = PathBuf::from(opts.path);
    let input = PathBuf::from(opts.input);
    let output = PathBuf::from(opts.output);
    let pre_command = PathBuf::from(opts.command);
    let conf = PathBuf::from(opts.config);
    //interact_with_qianwen::llm_trans_with_qianwen(&pre_command,&conf,&input);
    let content = fs::read_to_string(input).unwrap();

    let errors = exec_guarding(content, buf,output);
    // let content = serde_json::to_string_pretty(&errors).unwrap();
    // let _ = fs::write(opts.output, content);
}
