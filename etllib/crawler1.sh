#!/bin/bash
usage()
{
  echo "Usage: sh test.sh [-d dataset] [-o output]"
  echo "-d dataset"
  echo "    Directory name containing the *.tsv files"
  echo "-o output"
  echo "    Directory name where the json files will be created"
}

if [ "$#" -ne 4 ]
then
  echo "Error: Illegal number of parameters"
  usage
  exit
fi

if [ "$1" != "-d" ] || [ "$3" != "-o" ]
then
  usage
  exit
fi

if [ -d "$4" ]
then
  if [ "$(ls -A $4/)" ]
  then
    rm -r $4/*
  fi
fi

count=0

for filename in $2/*.tsv
do
    while read line || [[ -n "$line" ]]
    do
        count=$((count+1))
        echo "$line" > temp.tsv
        tsvtojson -t temp.tsv -j $4/$count.json -c colheaders.txt -o employment -e encoding.txt
        echo "JSON file $count created."
    done < "$filename"
done

rm temp.tsv
echo "Total employments: $count"
