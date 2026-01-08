.PHONY: build test up down logs

build:
	mvn package

test:
	mvn test

up:
	docker compose up --build -d

down:
	docker compose down

logs:
	docker compose logs -f $${service}

