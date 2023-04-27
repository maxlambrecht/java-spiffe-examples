#!/bin/bash

url="localhost:8001/tasks"
status_code=$(curl -sL -w "%{http_code}\\n" "$url" -o /dev/null)
if [[ "$status_code" =~ ^2 ]]; then
  echo -e "\033[32mHTTP Status: $status_code\033[0m"
else
  echo -e "\033[31mHTTP Status: $status_code\033[0m"
fi
