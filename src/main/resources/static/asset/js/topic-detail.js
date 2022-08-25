$(function (){
    renderTable();
})

function renderTable(){
    const $container=$(".table-topic-detail");
    $container.html("");
    let html="";
    if (vocabs){
        html+=` <thead class="text-center">
                <tr>
                  <th class="align-top">Id</th>
                  <th class="align-top">Từ</th>
                  <th class="align-top">Loại từ</th>
                  <th class="align-top">Phiên âm</th>
                  <th class="align-top">Nghĩa</th>
                </tr>
                </thead>
                <tbody class="table-vocab-content">`;
        vocabs.forEach(vocab => {
            html += `  <tr class="text-center">
                  <td>${vocab.id}</td>
                  <td>${vocab.word}</td>
                  <td>${vocab.type}</td>
                  <td>${vocab.phonetic}</td>
                  <td>${vocab.vnMeaning}</td>
                </tr>`
        });
        html+=`</tbody>`;
    }else {
        html+=`  <thead class="text-center">
                <tr>
                  <th class="align-top">Id</th>
                  <th class="align-top">Nội dung</th>
                  <th class="align-top">Phiên âm</th>
                  <th class="align-top">Nghĩa</th>
                </tr>
                </thead>
                <tbody class="table-sentence-content">`;
        sentences.forEach(sen => {
            html += ` <tr class="text-center">
                  <td>${sen.id}</td>
                  <td>${sen.content.replaceAll("_"," ")}</td>
                  <td>${sen.phonetic.replaceAll("_"," ")}</td>
                  <td>${sen.vnSentence}</td>
                </tr>`
        });
        html+=`</tbody>`;
    }
    $container.append(html);
}