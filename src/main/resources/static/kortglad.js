$('#dommer').bind('input', function() {
    var value = $(this).val();
    console.debug(value);
    handleInputChange(value);
});

$(document).ready(function() {
    if(getFiksId() !== undefined){
        $("#dommer").val("https://www.fotball.no/fotballdata/person/dommeroppdrag/?fiksId="+getFiksId());
        doFetch();
    }
});

$('#fetch').on('click', function() {
    doFetch();
});

function doFetch(){
    var fiksId = getFiksId();
    if(fiksId !== undefined) {
        fetchStats(fiksId);
    }
}

function getFiksId(){
     return window.location.hash.substring(1).split("=")[1];
}

function handleInputChange(value){
    $("#dommer").removeClass("is-valid")
    var fiksId = extractFiksId(value);
    if(fiksId != null){
        setFiksId(fiksId);
        $("#dommer").addClass("is-valid")
    }
}

function fetchStats(fiksId){
    $("#response").hide();
    clearData();
    $("#fetch").html("<i class='fa fa-spinner fa-spin fa-fw'></i>Henter statistikk")
    var jqxhr = $.get( "http://localhost:8080/referee/"+fiksId, function(data) {
        // console.debug(data);
        handleResponse(data);
    })
            .done(function() {
                // alert( "second success" );
            })
            .fail(function() {
                // alert( "error" );
            })
            .always(function() {
                $("#fetch").html("Hent statistikk")
            });
}

function extractFiksId(url){
    var urlParams = new URL(url);
    var fiksId = urlParams.searchParams.get('fiksId');
    return fiksId;
}

function setFiksId(fiksId){
    var url = new URL(window.location);
    url.searchParams.set("fiksId",fiksId);
    window.location.hash = "fiksId="+fiksId;
}

function handleResponse(data) {
    $("#response").show();
    $("#dommernavn").text(data.refereeName)
    handleKortdata(data.totals, data.averages)
    handleMatches(data.matches)

}

function handleKortdata(totals, avg){
    $('<tr>').append(
            $('<th scope="row">').text("Gult"),
            $('<td>').text(avg.yellow.toFixed(2)),
            $('<td>').text(totals.yellow)
    ).appendTo('#kortdata');
    $('<tr>').append(
            $('<th scope="row">').text("Gult nr 2"),
            $('<td>').text(avg.yellowToRed.toFixed(2)),
            $('<td>').text(totals.yellowToRed)
    ).appendTo('#kortdata');
    $('<tr>').append(
            $('<th scope="row">').html("R&oslash;dt"),
            $('<td>').text(avg.red.toFixed(2)),
            $('<td>').text(totals.red)
    ).appendTo('#kortdata');
}

function handleMatches(matches){
    $("#viskamper").html("Vis statistikk per kamp ("+matches.length + ")")
    $.each(matches, function(i, match){
        var $tr = $('<tr>').append(
                $('<td>').text(moment(match.kickoff).format("DD.MM.YYYY HH:mm")),
                $('<td>').html(match.home +" - "+ match.away),
                $('<td class="small">')
                        .html("R&oslashde: "+ match.cardStats.red+ "<br/>Gult nr 2: "+match.cardStats.yellowToRed +"</br>Gule: "+match.cardStats.yellow),
                $('<td>').html("<a href="+match.url+">fotball.no</a>")

        ).appendTo('#kampdata');
    });
}

function clearData(){
    $("#kampdata").empty();
    $("#kortdata").empty();
}