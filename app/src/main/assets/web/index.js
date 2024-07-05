/*
 Android와 JavaScript 간의 상호작용을 가능하게 하기 위한 설정
 */
window.androidObj = function AndroidClass() {};
//var selectedlist = [];


var svgBody = document.getElementById('div').innerHTML;
var node = '.selected {fill: lightblue;}';
var nodex = '.default{fill:#e9e9e9;}';
var svg = document.getElementsByTagName('svg')[0];

var addingValue = nodex + node;
document.addEventListener("click", doSomething);

var svgOutput = document.getElementById("div").outerHTML;

var query = '*[id^=section]';

var tablePathList = document.querySelectorAll(query);
var table;
for (table = 0; table < tablePathList.length; table++) {
    tablePathList[table].removeAttribute('style');
    if (tablePathList[table].classList.contains('seat')) {
        document.getElementById(tablePathList[table].id).classList.add('default');
    }
}


let lastClickedId = null;

window.androidObj.handleAndroidClick = function(clickedId) {
    console.log("Clicked ID:", clickedId);

    // 클릭된 섹션이 이전에 클릭한 것과 동일한 경우
    if (clickedId === lastClickedId) {
        resetAllElements(); // 모든 요소를 초기화 (어두운 상태로)
        lastClickedId = null; // 마지막 클릭 섹션 ID 초기화

    } else {
        // 새로 클릭된 섹션 처리
        // 클릭된 섹션 그룹의 모든 요소 선택
        var selectedQuery = '#' + clickedId + ' *[id^=section]';
        var sectionElements = document.querySelectorAll(selectedQuery);

        // 클릭된 섹션의 요소들을 밝게 설정
        sectionElements.forEach(function(element) {
            element.setAttribute('fill-opacity', '1.0');
        });

        // 클릭된 섹션의 배경을 반투명한 회색으로 설정
        var backgroundRect = document.querySelector('*[id^=Rectangle]');
        if (backgroundRect) {
            backgroundRect.setAttribute('fill', '#666666');
            backgroundRect.setAttribute('fill-opacity', '0.5');
        }

        // 클릭된 섹션 이외의 모든 섹션을 어둡게 설정
        var allSections = document.querySelectorAll('*[id^=section]');
        allSections.forEach(function(element) {
            if (!element.closest('#' + clickedId)) {
                element.setAttribute('fill-opacity', '0.5');
            }
        });

        lastClickedId = clickedId; // 마지막 클릭 섹션 ID 업데이트
    }
};

function resetAllElements() {
    // 모든 섹션 요소들을 어둡게 설정
    var allSections = document.querySelectorAll('*[id^=section]');
    allSections.forEach(function(element) {
        element.setAttribute('fill-opacity', '0.5');
    });

    // 배경인 'Rectangle' 요소를 흰색으로 초기화
    var backgroundRect = document.querySelector('*[id^=Rectangle]');
    if (backgroundRect) {
        backgroundRect.setAttribute('fill', 'white');
        backgroundRect.setAttribute('fill-opacity', '1.0');
    }
}


function callAndroidMethod(sectionName) {
    window.androidObj.handleAndroidClick(sectionName);
}

function doSomething(e) {
    if (e.target !== e.currentTarget) {
        var clickedItem = e.target.id;
        var itemName;
        var item;
        window.androidObj.textToAndroid(clickedItem);

        if (clickedItem === 'RedSection' || clickedItem === 'BlueSection' || clickedItem === 'GraySection') {
            window.androidObj.zoomToSection(clickedItem);
            console.log(clickedItem)
        }else{
            for (item = 0; item < tablePathList.length; item++) {
                if (clickedItem === tablePathList[item].id) {
                    var clickedSvgPath = document.getElementById(clickedItem);
                    clickedSvgPath.classList.toggle("selected");
                    itemName = e.target.querySelector('title').innerHTML;
                }
            }
            console.log("Hello " + clickedItem);
            window.androidObj.textToAndroid(itemName);
            document.getElementById('l_value').innerHTML = itemName;
        }
    }
    e.stopPropagation();
}

function updateFromAndroid(message) {
    document.getElementById('l_value').innerHTML = message;
}