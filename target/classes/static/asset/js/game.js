$(function () {
  countUp();
  var correctCards = 0;
  init();
});

let timeCount;

function countUp() {
  const $time = $(".test-countup-number");
  let time = 0;
  $time.text(`${time}`);
  timeCount = setInterval(function () {
    time++;
    $time.text(`${time}`);
    // playClockSound();
  }, 1000);
}

function playClockSound() {
  $(".clock-sound")[0].load();
  $(".clock-sound")[0].onloadeddata = function () {
    $(".clock-sound")[0].play();
  };
}

function init() {
  // Hide the success message
  $("#successMessage").hide();
  $("#successMessage").css({
    left: "580px",
    top: "250px",
    width: 0,
    height: 0,
  });

  // Reset the game
  correctCards = 0;
  $(".word-field").html("");
  $(".sentence-filed").html("");

  // Create the pile of shuffled cards
  var words = [
    "name",
    "first name",
    "family name",
    "family",
    "age",
    "address",
    "live",
    "street",
    "countryside",
    "born",
  ];

  for (var i = 0; i < words.length; i++) {
    $("<div>" + words[i] + "</div>")
      .data("number", i)
      .attr("id", "word" + i)
      .appendTo(".word-field")
      .draggable({
        containment: ".viewport",
        stack: ".word-field div",
        cursor: "move",
        revert: true,
        drag: function (event, ui) {
          $(".word-field div.is-wrong").removeClass("is-wrong");
          $(".sentence-field div.is-wrong").removeClass("is-wrong");
        },
      })
      .droppable({
        accept: ".sentence-field div",
        // hoverClass: "hovered",
        drop: handleCardDrop,
      });
  }

  // Create the card slots
  var sentences = [
    "a word or words that a particular person, animal, place or thing is known by",
    "a name that was given to you when you were born, that comes before your family name",
    "​the part of your name that shows which family you belong to",
    "a group consisting of one or two parents and their children",
    " the number of years that a person has lived or a thing has existed",
    "details of where somebody lives or works and where letters, etc. can be sent",
    "to have your home in a particular place",
    "a public road in a city or town that has houses and buildings on one side or both sides",
    "land outside towns and cities, with fields, woods, etc.",
    "to come out of your mother’s body at the beginning of your life",
  ];
  for (var i = 0; i < sentences.length; i++) {
    $("<div>" + sentences[i] + "</div>")
      .data("number", i)
      .attr("id", "sentence" + i)
      .appendTo(".sentence-field")
      .draggable({
        containment: ".viewport",
        stack: "#cardPile div",
        cursor: "move",
        revert: true,
        drag: function (event, ui) {
          $(".word-field div.is-wrong").removeClass("is-wrong");
          $(".sentence-field div.is-wrong").removeClass("is-wrong");
        },
      })
      .droppable({
        accept: ".word-field div",
        // hoverClass: "hovered",
        drop: handleCardDrop,
      });
  }
}

function handleCardDrop(event, ui) {
  //Grab the slot number and card number
  var word = $(this).data("number");
  var sentence = ui.draggable.data("number");

  //If the cards was dropped to the correct slot,
  //change the card colour, position it directly
  //on top of the slot and prevent it being dragged again
  if (word === sentence) {
    ui.draggable.addClass("is-correct").delay(500).hide(500);
      $(this).addClass("is-correct").delay(500).hide(500);
      
    
    //This prevents the card from being
    //pulled back to its initial position
    //once it has been dropped
    ui.draggable.draggable("option", "revert", false);
    correctCards++; //increment keep track correct cards
  } else {
    ui.draggable.addClass("is-wrong");
    $(this).addClass("is-wrong");
  }

  //If all the cards have been placed correctly then
  //display a message and reset the cards for
  //another go
  if (correctCards === 10) {
    $("#successMessage").show();
    $("#successMessage").animate({
      left: "380px",
      top: "200px",
      width: "400px",
      height: "150px",
      opacity: 1,
    });
  }
}
