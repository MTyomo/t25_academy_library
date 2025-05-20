function closePopup() {
  // ID が customPopup のポップアップを閉じる
  const customPopup = document.getElementById("customPopup");
  if (customPopup) {
    customPopup.style.display = "none";
  }

  // クラス名が popup のポップアップを閉じる（汎用）
  const classPopup = document.querySelector(".popup");
  if (classPopup) {
    classPopup.style.display = "none";
  }
}

// 今回変更文
let deleteTargetId = null;

function openDeletePopup(id, title, isbn) {
  deleteTargetId = id;
  // document.getElementById('bookId').textContent = id;
  document.getElementById('bookTitle').textContent ="タイトル: " + title;
  document.getElementById('bookIsbn').textContent = "ISBN: " + isbn;
  document.getElementById('deleteConfirmPopup').style.display = 'flex';
}

// function closePopup() {
//   document.getElementById('deleteConfirmPopup').style.display = 'none';
// }

function showSuccessPopup() {
  document.getElementById('deleteSuccessPopup').style.display = 'flex';
  setTimeout(() => {
    document.getElementById('deleteSuccessPopup').style.display = 'none';
    location.reload(); // 一覧を再表示
  }, 2000);
}

function deleteBook(id) {
  window.location.href = `delete/${id}`;
  // fetch(`delete/${id}`, {
  //   method: 'POST'
  // })
  //   .then(response => {
  //     if (response.ok) {
  //       closePopup();
  //       showSuccessPopup();
  //     } else {
  //       return response.text().then(errorMessage => {
  //         alert(errorMessage); // Controllerが返す具体的なメッセージを表示
  //       });
  //     }
  //   })
  //   .catch(error => {
  //     console.error("通信エラー:", error);
  //     alert("削除に失敗しました（通信エラー）");
  //   });
}



// function showSuccessPopup() {
//   document.getElementById('successPopup').style.display = 'flex';
// }

// function closeSuccessPopupAndReload() {
//   document.getElementById('successPopup').style.display = 'none';
//   location.reload();
// }



