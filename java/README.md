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

### 2. 修改并运行 prep_ae.py (D:\Dev\ProjectsNew\NLP\BERT-for-RRC-ABSA\pytorch-pretrained-bert\preprocessing\prep_ae.py) ，将xml文件转换为json 文件(\BERT-for-RRC-ABSA\pytorch-pretrained-bert\ae\assurance\test.json)，格式如：
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

备注：

有可能需要安装nltk, numpy。相关命令：
```
sudo pip3 install numpy
pip3 install nltk
ls /usr/share/
mkdir -p /usr/share/nltk_data/tokenizers
cp /mnt/c/Users/wuyijun/Downloads/punkt.zip /usr/share/nltk_data/tokenizers/
cd /usr/share/nltk_data/tokenizers/
unzip punkt.zip
rm -f punkt.zip
```
pip3 install等命令有可能需要sudo

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

可能需要安装。相关命令
```
sudo pip3 install pytorch-pretrained-bert
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



待整理：
1  pwd

2  ls

3  cd /home

4  ls

5  cd wuyijun

6  ls

7  mkdir inst1dir

8  ls

9  exit

10  ls /home/wuyijun/

11  exit

12  ls /home/wuyijun/

13  exit

14  pwd

15  ls

16  yum

17  apt

18  python --version

19  python3

20  pip3 install torch==1.8.1+cu111 torchvision==0.9.1+cu111 torchaudio==0.8.1 -f https://download.pytorch.org/whl/torch_stable.html

21  sudo apt install python3-pip

22  pip3 install torch==1.8.1+cu111 torchvision==0.9.1+cu111 torchaudio==0.8.1 -f https://download.pytorch.org/whl/torch_stable.html

23  cd /mnt/d/Dev

24  cd Projectsnew

25  cd ..

26  cd ProjectsNew

27  ls

28  cd NLP

29  ls

30  cd BERT-for-RRC-ABSA/

31  ls

32  cd transformers/

33  ls

34  bash script/run_ft.sh

35  exit

36  cd /mnt/d/Dev/ProjectsNew/NLP/BERT-for-RRC-ABSA/transformers

37  ./script/run_ft.sh

38  vi ./script/run_ft.sh

39  pwd

40  ls

41  cd /home

42  ls

43  cd wuyijun

44  ls

45  pwd

46  ll

47  mkdir Dev

48  cd Dev

49  mkdir ProjectsNew

50  cd ProjectsNew/

51  mkdir NLP

52  ls

53  cd NLP

54  mkdir BERT-for-RRC-ABSA

55  cd BERT-for-RRC-ABSA/

56  git clone https://github.com/howardhsu/BERT-for-RRC-ABSA.git

57  ls

58  cd ..

59  ls

60  rm -rf BERT-for-RRC-ABSA/

61  git clone https://github.com/howardhsu/BERT-for-RRC-ABSA.git

62  exit

63  cd /home/wuyijun/

64  ls

65  exit

66  pwd

67  cd /mnt/d/Dev/ProjectsNew/

68  ls

69  cd NLP

70  cd BERT-for-RRC-ABSA/

71  ls

72  cd pytorch-pretrained-bert/

73  ls

74  cd script/

75  bash run_absa.sh ae pt_bert-base-uncased_amazon_yelp laptop pt_ae 2 0

76  bash run_absa.sh ae pt_bert-base-uncased_amazon_yelp laptop pt_ae 15 0

77  bash run_absa.sh asc pt_bert-base-uncased_amazon_yelp laptop pt_asc 10 0

78  cd ..

79  ls

80  cd preprocessing/

81  ls

82  python prep_ae.py

83  pip3 install nltk

84  python prep_ae.py

85  python

86  ls /usr/share/

87  mkdir -p /usr/share/nltk_data/tokenizers

88  cp /mnt/c/Users/wuyijun/Downloads/punkt.zip /usr/share/nltk_data/tokenizers/

89  cd /usr/share/nltk_data/tokenizers/

90  unzip punkt.zip

91  ll

92  rm -f punkt.zip

93  cd -

94  python prep_ae.py

95  cd ..

96  cd script

97  bash run_absa.sh ae pt_bert-base-uncased_amazon_yelp assurance pt_ae 2 0

98  cd ../preprocessing/

99  python prep_ae.py

100  cd ../script/

101  bash run_absa.sh ae pt_bert-base-uncased_amazon_yelp assurance pt_ae 2 0

102  cd ../preprocessing/

103  python prep_ae.py

104  cd script

105  cd ../script/

106  bash run_absa.sh ae pt_bert-base-uncased_amazon_yelp assurance pt_ae 2 0

107  bash run_absa.sh asc pt_bert-base-uncased_amazon_yelp assurance pt_asc 2 0

108  history