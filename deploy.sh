mvn clean package
tag="$(date +%Y%m%d%H%M%S)";
tsf_repo="websocket-gateway"
docker build -t ccr.ccs.tencentyun.com/tsf_100002742997/$tsf_repo:"$tag" .
docker push ccr.ccs.tencentyun.com/tsf_100002742997/$tsf_repo:"$tag"
