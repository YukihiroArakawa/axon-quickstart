.PHONY: start-api1 start-api2 start-all stop-api1 stop-api2 stop-all logs-api1 logs-api2 logs-all status

LOG_DIR ?= logs
API1_PID := $(LOG_DIR)/api1.pid
API2_PID := $(LOG_DIR)/api2.pid
API1_LOG := $(LOG_DIR)/api1.log
API2_LOG := $(LOG_DIR)/api2.log

# Configure distinct ports to avoid conflicts
API1_PORT ?= 8081
API2_PORT ?= 8082

start-api1:
	@mkdir -p $(LOG_DIR)
	@echo "Starting api1 (command service) ..."
	@nohup mvn -f api1/pom.xml spring-boot:run -Dspring-boot.run.arguments="--server.port=$(API1_PORT)" \
		> $(API1_LOG) 2>&1 & echo $$! > $(API1_PID)
	@echo "api1 logs -> $(API1_LOG) (port $(API1_PORT))"

start-api2:
	@mkdir -p $(LOG_DIR)
	@echo "Starting api2 (query service) ..."
	@nohup mvn -f api2/pom.xml spring-boot:run -Dspring-boot.run.arguments="--server.port=$(API2_PORT)" \
		> $(API2_LOG) 2>&1 & echo $$! > $(API2_PID)
	@echo "api2 logs -> $(API2_LOG) (port $(API2_PORT))"

start-all: start-api1 start-api2

stop-api1:
	@if [ -f $(API1_PID) ]; then \
		PID=$$(cat $(API1_PID)); \
		if kill $$PID >/dev/null 2>&1; then \
			echo "Stopped api1 (PID $$PID)"; \
		else \
			echo "api1 not running (PID $$PID)"; \
		fi; \
		rm -f $(API1_PID); \
	else \
		echo "No PID file for api1"; \
	fi
	@PORT_PIDS=$$(lsof -t -i:$(API1_PORT) 2>/dev/null); \
	if [ -n "$$PORT_PIDS" ]; then \
		echo "Killing processes on port $(API1_PORT): $$PORT_PIDS"; \
		kill $$PORT_PIDS >/dev/null 2>&1 || true; \
	fi

stop-api2:
	@if [ -f $(API2_PID) ]; then \
		PID=$$(cat $(API2_PID)); \
		if kill $$PID >/dev/null 2>&1; then \
			echo "Stopped api2 (PID $$PID)"; \
		else \
			echo "api2 not running (PID $$PID)"; \
		fi; \
		rm -f $(API2_PID); \
	else \
		echo "No PID file for api2"; \
	fi
	@PORT_PIDS=$$(lsof -t -i:$(API2_PORT) 2>/dev/null); \
	if [ -n "$$PORT_PIDS" ]; then \
		echo "Killing processes on port $(API2_PORT): $$PORT_PIDS"; \
		kill $$PORT_PIDS >/dev/null 2>&1 || true; \
	fi

stop-all: stop-api1 stop-api2

logs-api1:
	@if [ -f $(API1_LOG) ]; then \
		tail -f $(API1_LOG); \
	else \
		echo "Log file not found: $(API1_LOG)"; \
	fi

logs-api2:
	@if [ -f $(API2_LOG) ]; then \
		tail -f $(API2_LOG); \
	else \
		echo "Log file not found: $(API2_LOG)"; \
	fi

logs-all:
	$(MAKE) logs-api1 & \
	PID1=$$!; \
	$(MAKE) logs-api2; \
	kill $$PID1 >/dev/null 2>&1 || true

status:
	@printf "api1: "
	@if [ -f $(API1_PID) ] && ps -p $$(cat $(API1_PID)) >/dev/null 2>&1; then \
		echo "RUNNING (PID $$(cat $(API1_PID)))"; \
	else \
		echo "STOPPED"; \
	fi
	@printf "api2: "
	@if [ -f $(API2_PID) ] && ps -p $$(cat $(API2_PID)) >/dev/null 2>&1; then \
		echo "RUNNING (PID $$(cat $(API2_PID)))"; \
	else \
		echo "STOPPED"; \
	fi
