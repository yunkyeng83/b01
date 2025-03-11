// 댓글 상세 조회
async function get1(bno) {
    // get api 메소드 호출
    const result = await axios.get(`/replies/list/${bno}`);
    //console.log('get1:', result);

    //return result.data;
    return result;
}

// 댓글 목록 리스트 조회
async function getList({bno, page, size, goLast}) {
    // ex) /replies/list/3?page=1&size=10
    const result = await axios.get(`/replies/list/${bno}`, {params: {page, size}} );  // 첫번째 인수는 url, 두번째 인수는 get으로 전달할 변수

    if(goLast){
        let total = result.data.total
        if(total == 0){
            lastPage = 1
            return
        }else{
            const lastPage = parseInt(Math.ceil(total/size))
        }
        return getList({bno:bno, page:lastPage, size:size})
    }

    return result.data
}

async function addReply(replyObj){
    const response = await axios.post(`/replies/`,replyObj)
    return response.data
}

async function getReply(rno){
    const response = await axios.get(`/replies/${rno}`)
    return response.data
}

async function modifyReply(replyObj){
    const response = await axios.put(`/replies/${replyObj.rno}`, replyObj)
    return response.data
}

async function removeReply(rno){
    const response = await axios.delete(`/replies/${rno}`)
    return response.data
}





