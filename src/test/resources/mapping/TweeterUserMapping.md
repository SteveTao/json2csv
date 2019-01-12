# tweerter user mapping
## tw_user
- config

```json
{
  "filter": {
    "match":{}
  },
  "root": "user",
  "comment": "tweeter用户表"
}
```

- fields

```csv
id,,,主健
name,,,用户名
screen_name,,,显示名
location,,,位置 
description,,varchar(65535),描述
url,,varchar(500),个人网址
protected,,boolean,是否受保护
followers_count,,int,粉丝数量
friends_count,,int,好友数量
listed_count,,int,未知字段
created_at,create_time,timestamp,创建时间
favourites_count,,,关注数量
utc_offset,,,时区位移
time_zone,,,时区
```