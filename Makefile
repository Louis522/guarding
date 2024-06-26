.PHONY: docs_check docs fmt_check fmt clippy_check clippy build test check clean

docs_check:
	cargo doc --no-deps --document-private-items --all-features # TODO: docs check won't fail if there is warning, should be fixed later

docs:
	cargo doc --no-deps --document-private-items --all-features --open

fmt_check:
	cargo fmt --all -- --check

fmt:
	cargo fmt --all

clippy_check:
	cargo clippy --all-features --all-targets

clippy:
	cargo clippy --all-features --all-targets --fix

build:
	cargo build --all-features --all-targets

test:
	cargo test --all-features

check: fmt_check clippy_check docs_check build test 

clean:
	cargo clean
