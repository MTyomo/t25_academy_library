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
