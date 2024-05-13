.PHONY: build

run-dist:
	./build/install/app/bin/app

build:
	./gradlew build

run:
	./gradlew run

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

report:
	./gradlew jacocoTestReport