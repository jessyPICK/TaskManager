# Makefile

# Variables
SRC_DIR := src
DOC_DIR := doc


RM := rm -rf
MV := mv


move_assets:
	@$(MV) $(SRC_DIR)/Tasks .

clean_assets:
	@$(MV) Tasks $(SRC_DIR)/
	@$(RM) build

build: move_assets
	@javac -cp "src/.;src/lib/*" src/*.java -d build
run:
	@java -cp "build;src/lib/*" TaskManager
clean: clean_assets