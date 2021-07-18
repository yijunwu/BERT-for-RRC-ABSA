# 大致流程：
1. 运行java程序，从csv生成xml文件，
2. 运行 preprocessing/prep_ae.py，将xml文件转换为json 文件
3. 运行 script/run_absh.sh ae ... ，产生AE(Aspect Extraction)结果
4. 运行 script/run_absh.sh asc ... ，产生ASC(Aspect Sentiment Classification)结果
5. 运行 Exporter，将结果导出

# 具体过程：
### 1. 准备xml文件，格式如：

```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<sentences>
    <sentence id="0">
        <text>q10.Could you specify the reason why you would or would not recommend Trade Assurance Service?</text>
    </sentence>
    <sentence id="1">
        <text>everything you could imagine to purchase to sell or buy for yourself in one place</text>
    </sentence>
	<!-- ... -->
</sentences>
```
文件放到 \BERT-for-RRC-ABSA\pytorch-pretrained-bert\ae\assurance\ae_test.xml

如果输入文件是csv格式，可以用这个程序转换：AspectExtractionInputConverter

### 2. 运行 prep_ae.py (D:\Dev\ProjectsNew\NLP\BERT-for-RRC-ABSA\pytorch-pretrained-bert\preprocessing\prep_ae.py) ，将xml文件转换为json 文件(\BERT-for-RRC-ABSA\pytorch-pretrained-bert\ae\assurance\test.json)，格式如：
```
   {
   "0": {
   "id": 0,
   "label": [
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O"
   ],
   "sentence": [
   "q10.Could",
   "you",
   "specify",
   "the",
   "reason",
   "why",
   "you",
   "would",
   "or",
   "would",
   "not",
   "recommend",
   "Trade",
   "Assurance",
   "Service",
   "?"
   ]
   },
   "1": {
   "id": 1,
   "label": [
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O",
   "O"
   ],
   "sentence": [
   "everything",
   "you",
   "could",
   "imagine",
   "to",
   "purchase",
   "to",
   "sell",
   "or",
   "buy",
   "for",
   "yourself",
   "in",
   "one",
   "place"
   ]
   },
   ...
   }
```

命令：

```
cd /mnt/d/Dev/ProjectsNew/NLP
cd BERT-for-RRC-ABSA/pytorch-pretrained-bert/
cd preprocessing/

python prep_ae.py
```

### 3. 运行 script/run_absa.sh ae ... ，产生AE(Aspect Extraction)结果

命令：
```
cd /mnt/d/Dev/ProjectsNew/NLP
cd BERT-for-RRC-ABSA/pytorch-pretrained-bert/
cd script/

bash run_absa.sh ae pt_bert-base-uncased_amazon_yelp assurance pt_ae 2 0
```

参数说明：
```
task=$1
bert=$2
domain=$3
run_dir=$4
runs=$5
```

### 4. 运行 AspectExtractionResultExporter ，将结果导出为 prediction_terms.txt

### 5. 运行 AspectSentimentClassificationInputConverter，将 prediction_terms.txt 转换为 asc_test.json

### 6. 复制文件，准备数据文件集

   BERT-for-RRC-ABSA\pytorch-pretrained-bert\asc\assurance\test.json

   train.json

   dev.json


### 7. 运行 script/run_absh.sh asc ... ，产生ASC(Aspect Sentiment Classification)结果

BERT-for-RRC-ABSA\pytorch-pretrained-bert\run\pt_asc\assurance\2\predictions.json

命令：
```
cd /mnt/d/Dev/ProjectsNew/NLP
cd BERT-for-RRC-ABSA/pytorch-pretrained-bert/
cd script/

bash run_absa.sh asc pt_bert-base-uncased_amazon_yelp assurance pt_asc 2 0
```

### 8. 运行 AspectSentimentClassificationResultExporter，将结果导出

输入：

BERT-for-RRC-ABSA\pytorch-pretrained-bert\run\pt_asc\assurance\2\predictions.json

输出：

BERT-for-RRC-ABSA\java\src\main\resources\prediction_sentiment.txt

# 附录

sudo apt-get update
sudo apt install python3-pip
pip3 install torch==1.8.1+cu111 torchvision==0.9.1+cu111 torchaudio==0.8.1 -f https://download.pytorch.org/whl/torch_stable.html