#!/bin/bash
echo "로컬 실행 (Oracle)"
./gradlew bootRun --args='--spring.profiles.active=local'
