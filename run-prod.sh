#!/bin/bash  
echo "운영 실행 (PostgreSQL)"
./gradlew bootRun --args='--spring.profiles.active=prod'
