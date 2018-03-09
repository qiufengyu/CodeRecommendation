## Code Recommendation, Algorithm and System Design

### Publication

The related paper entitled *Code Recommendation with Natural Language Tags and Other Heterogeneous Data* is accepted by [CSAI-2017](http://www.csai.org/). Download from [ACM](http://delivery.acm.org/10.1145/3170000/3168407/p137-Qiu.pdf).

### Citing
```
@inproceedings{Qiu2017CodeRW,
  title={Code Recommendation with Natural Language Tags and Other Heterogeneous Data},
  author={Fengyu Qiu and Weiyi Ge and Xinyu Dai},
  booktitle={CSAI 2017},
  year={2017}
}
```

Some description please refer to [my blog](https://qiufengyu.github.io/2016/11/12/code-recommendation/).

### A final project of bachelor in NJU

Code data from [codereview](http://codereview.stackexchange.com/), active codes with >50 views

If you can read [an overview of recommender algorithms](https://buildingrecommenders.wordpress.com/2015/11/16/overview-of-recommender-algorithms-part-1/), I will be glad.

### Algorithm related

#### 1. Collaborative Filtering

similarity of user

#### 2. Content-Based Filtering

code similarity using code title, [word2vec](http://deeplearning4j.org/word2vec) involved, so maven required

#### 3. Code-User Tags Retrieval and Matching

simply match code tags and user tags

#### 4. User Tags Similarity Based Collaborative Filtering

similarity calculated from user tags, calculated similarity of tags required

#### 5. User-Relation Enhancement

if userA follows userB, the question proposed by userB may also attract userA

#### 6. User Reputation Reduced

if userA is famous in the community, his post may attract more user. His influence should be reduced

### Future Work

using code feature together to recommend code
