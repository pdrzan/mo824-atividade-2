#!/bin/bash

# Define the target directory
TARGET_DIR="." # Current directory, change as needed

# Check if the target is a valid directory
if [ ! -d "$TARGET_DIR" ]; then
  echo "Error: Directory '$TARGET_DIR' not found."
  exit 1
fi

# Loop through files in the target directory
for FILE in "$TARGET_DIR"/*.txt; do
  # Check if the current item is a regular file
  if [ -f "$FILE" ]; then
    echo "Processing file: $FILE"
    /usr/bin/env /usr/lib/jvm/java-21-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/lucasborts/Documents/UNICAMP/Materias/Semestre_6/PO/T2/mo824-atividade-2/bin problems.qbf.solvers.GRASP_MAX_SC_QBF --instance "$FILE" --randomiterations 0 --firstimprovement false --linearBias false --alpha 0.05
    # Add your desired commands here to process each file
    # For example, to print the content:
    # cat "$FILE"
  fi
done