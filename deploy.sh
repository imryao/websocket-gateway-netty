mvn clean package
tag="$(date +%Y%m%d%H%M%S)"
repo="websocket-gateway"
docker build -t mirrors.tencentcloudcr.com/open/"$repo":"$tag" .
docker push mirrors.tencentcloudcr.com/open/"$repo":"$tag"
