$(function () {
  playWordSound();
});

function playWordSound() {
  const sound = document.querySelectorAll(".play-sound");
  sound.forEach(function (sound) {
    const btn = sound.childNodes[1];
    const mp3 = sound.childNodes[3];
    btn.addEventListener("click", function () {
      mp3.play();
    });
  });
}
