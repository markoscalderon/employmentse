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

total=0

for filename in $2/*.tsv
do
  output_dir=${filename%.tsv}
  output_dir=${output_dir##*/}
  output_dir=$4/$output_dir

  mkdir -p $output_dir

  count=0
  while read line
  do
    echo "$line" > temp.tsv
    #echo "tsvtojson -t temp.tsv -j $output_dir/res-$count.json -c colheaders.txt -o employment"
    tsvtojson -t temp.tsv -j $output_dir/res-$count.json -c colheaders.txt -o employment
    count=$((count+1))
  done < "$filename"

  total=$((total+count))
done

rm temp.tsv
echo "Total employments: $total"
