/**
 * @author Ricardo F. Teixeira <ricardo.teixeira@caixamagica.pt>
 *
 */

$(document).ready(function () {
    $('#cudf-form-parent').ajaxForm({
        beforeSend: function () {
            $("#myModalLabel").text("Software Dependencies Status");
            $("#myModal > div:last").empty();
            $("#myModal").modal('show');
        },
        success: function () {
            $("#myModal > div:last").append('<p style="font-weight:bold;">Ontology populated!</p>');
            $("#myModal").modal('show');
        }
    });

    $('.brand ').hover(function () {
        $('.brand').css('color', "rgb(51, 51, 51)");
    }, function () {
        $('.brand').css('color', "rgb(119, 119, 119)");
    });

    //
    // cudf
    //

    $('a[href^="#"]').click(function (event) {
        var id = $(this).attr("href");
        var target = $(id).offset().top;

        $('html, body').animate({
            scrollTop: target
        }, 500);

        event.preventDefault();
    });

    $('input[id=cudf-file-1]').change(function () {
        var path = $(this).val().replace("C:\\fakepath\\", "");
        $('#pretty-input-1').val(path);
    });

    $('#cudf-plus').click(
        function (event) {
            var id = $('#cudf-dynamic > div:last').attr("id");
            var increment = parseInt(id.substr(id.length - 1)) + 1;
            $('#cudf-dynamic').append($(
                '<div id="cudf-div-' + increment + '" class="form-inline" style="padding-top: 5px;"> \
					<input id="cudf-text-' + increment + '" type="text" name="node" placeholder="127.0.0.1"> \
					<input id="cudf-file-' + increment + '" type="file" name="file" class="hide ie_show"> \
					<div class="input-append ie_hide"> \
						<input id="pretty-input-' + increment + '" class="input-large" type="text" onclick="$(\'input[id=cudf-file-' + increment + ']\').click();" placeholder="/tmp/file.cudf"> \
						<a class="btn" onclick="$(\'input[id=cudf-file-' + increment + ']\').click();">Browse</a> \
					</div> \
				</div>').hide().fadeIn("slow"));

            $('input[id=cudf-file-' + increment + ']').change(function () {
                var path = $(this).val().replace("C:\\fakepath\\", "");
                $('#pretty-input-' + increment).val(path);
            });

            event.preventDefault();
        });

    $('#cudf-minus').click(function (event) {
        $('#cudf-dynamic > div:last').fadeOut("slow", function () {
            var id = $('#cudf-dynamic > div:last').attr('id');
            var number = parseInt(id.substr(id.length - 1));

            if (number > 2) {
                $(this).remove();
            }
        });

        event.preventDefault();
    });

    $('#cudf-submit').click(function (event) {
        var id = $('#cudf-dynamic > div:last').attr("id");
        var length = parseInt(id.substr(id.length - 1));

        for (var i = 1; i <= length; i++) {
            if ($('#cudf-text-' + i).val() == "" || $('#cudf-file-' + i).val() == "") {
                $('#cudf-div-' + i).addClass("control-group");
                $('#cudf-div-' + i).addClass("error");

                if ($('#cudf-warning-' + i).length == 0) {
                    $('#cudf-div-' + i).append('<span id="cudf-warning-' + i + '" class="help-inline">Please fill <b>all</b> the inputs!</span>');
                }

                event.preventDefault();
            }
        }
    });

    //
    // node
    //

    $('#business-cases').change(function (event) {
        $('#business-cases').removeClass("control-group");
        $('#business-cases').removeClass("error");
        $('#business-cases-warning').remove();
    });

    $('#node-text, #os-text').change(function (event) {
        $('#node-div').removeClass("control-group");
        $('#node-div').removeClass("error");
        $('#node-warning').remove();
    });

    $('#node-submit').click(function (event) {
        event.preventDefault();

        if ($('#node-text').val().replace(/\s/g, '') == "") {
            if ($('#node-warning').length == 0) {
                $('#node-div').addClass("control-group");
                $('#node-div').addClass("error");
                $('<span id="node-warning" class="help-inline">Please insert a node.</span>').insertAfter('#os-text');

                return false;
            }
        }

        if ($('#os-text option:selected').val() == "Operating System") {
            if ($('#node-warning').length == 0) {
                $('#node-div').addClass("control-group");
                $('#node-div').addClass("error");
                $('<span id="node-warning" class="help-inline">Please chosse an operating system for ' + $('#node-text').val().replace(/\s/g, '') + '.</span>').insertAfter('#os-text');

                return false;
            }
        }

        var exit = false;

        $('#table1 > tbody > tr').each(function () {
            console.log("D: " + $('#node-text').val().replace(/\s/g, ''));
            console.log("D: " + $(this).find("td:eq(2)").html().toString());

            if ($('#node-text').val().replace(/\s/g, '') == $(this).find("td:eq(2)").html().toString()) {
                exit = true;

                if ($('#node-warning').length == 0) {
                    $('#node-div').removeClass("control-group");
                    $('#node-div').removeClass("success");
                    $('#node-div').removeClass("error");
                    $('#node-warning').remove();

                    if ($('#node-warning').length == 0) {
                        $('#node-div').addClass("control-group");
                        $('#node-div').addClass("error");
                        $('<span id="node-warning" class="help-inline">Node already added.</span>').insertAfter('#os-text');

                        setTimeout(function () {
                            $('#node-div').removeClass("control-group");
                            $('#node-div').removeClass("success");
                            $('#node-div').removeClass("error");
                            $('#node-warning').remove();
                        }, 5000);
                    }
                }
            }
        });

        if (exit) {
            console.log("returning");
            return false;
        }

        var id = parseInt($("#table1").find('tbody:last tr').length);
        var node = $('#node-text').val();
        var os = $('#os-text option:selected').text();

        addRowTable(id, node, os);

        getExtractors();

        if ($('#node-warning').length == 0) {
            $('#node-div').addClass("control-group");
            $('#node-div').addClass("success");
            $('<span id="node-warning" class="help-inline">Node inserted!</span>').insertAfter('#os-text');

            setTimeout(function () {
                $('#node-div').removeClass("control-group");
                $('#node-div').removeClass("success");
                $('#node-div').removeClass("error");
                $('#node-warning').remove();
            }, 5000);
        }
    });

    $.ajax({
        type: 'GET',
        url: window.location.protocol + '//' + window.location.host + '/extractors/api/businesscases',
        dataType: 'json'
    }).done(function (data) {
            $.each(data.data, function (i, item) {
                $('<option value="' + item + '">' + item + '</option>').appendTo("#business-cases-select");
            });
        });

    function copy() {
        $('#hosts-extractors option').each(function () {
            $(this).remove();
        });

        $('#hosts-extractors').append('<option selected="selected">Nodes</option>');

        $('#hosts-selected > option').each(function () {
            var val = $(this).val();

            if (val != null) {
                $('#hosts-extractors').append('<option value="' + val + '">' + val + '</option>');
            }
        });
    }

    function disable(isDisable) {
        $('#hosts-extractors').attr("disabled", isDisable);
        $('#extractors-multiple').attr("disabled", isDisable);
        $('#extractors-selected').attr("disabled", isDisable);
    }

    function move(src, dst) {
        $(src + " option:selected").each(function () {
            var value = $(this).val();

            if ($(dst + " option[value='" + value + "']").length == 0) {
                $(dst).append($("<option></option>").attr("value", value).text(value));
            }

            $(this).remove();
        });
    }

    //
    // generate sample data
    //
    var count = $('.bar').width();
    var control = true;

    window.setInterval(function () {
        if ($('.bar')[0].style.width != "100%") {
            $('.bar').css('width', count + "%");
            count += 20;
        }

        else {
            $('.progress').removeClass('active');
            $('.progress').removeClass('progress-striped');
            $('.progress').fadeOut('slow');
            $('#completed').show();

            if ($('#completed').is(':visible') && control) {
                $('#completed').append('<strong>Success!</strong> Extraction completed.');
                control = false;
            }
        }

    }, 1000);

    function getExtractors() {
        $.ajax({
            type: 'GET',
            url: window.location.protocol + '//' + window.location.host + '/extractors/api/extractors',
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                $('#table1 > tbody > tr').each(function () {
                    var os = $(this).find("td:eq(3)").html();
                    var node = $(this).find("td:eq(2)").html().toString().replace(/\./g, '');

                    $.each(data, function (i, item) {
                        if (os == item.os) {
                            $(item.extractor).each(function (i, extractor) {
                                $('#available-extractors-' + node).append('<li><label class="checkbox"><input id="checkbox-' + node + '" type="checkbox"> ' + extractor + '</label></li>');
                            });
                        }
                    });
                });

                $.each($('input[id^="checkbox-"]'), function (key, group) {
                    $('.dropdown-toggle').dropdown();
                    $('.dropdown, .dropdown label, .dropdown input').click(function (event) {
                        event.stopPropagation();
                    });
                });
            }
        });

        $("#table1").trigger("update");

        $('#table1').tablesorter({
            sortList: [
                [1, 0],
                [2, 0],
                [3, 0]
            ],
            onRenderHeader: function (index) {
                this.wrapInner('<span class="icons"></span>');
            }
        });
    }

    function addRowTable(id, node, os) {
        var node_tmp = node.toString().replace(/\./g, '');

        tmp = '<tr id="row-' + node_tmp + '"><td><label class="checkbox"><input type="checkbox"> </label></td><td>' + id + '</td><td>' + node +
            '</td>' + '<td>' + os + '</td><td><div class="dropdown"><a id="select-extractor-' + node_tmp + '" class="btn btn-link dropdown-toggle" data-toggle="dropdown" \
		style="text-decoration: none;"><i class="icon-plus"></i></a><ul id="available-extractors-' + node_tmp + '" class="dropdown-menu" \
		style="padding: 10px;"><li style="margin-bottom: 10px;"> Available extractors</li></ul></div></td><td>\
		<a id="close-' + node_tmp + '" class="btn btn-link close dropdown-toggle" style="text-decoration: none; color: red;"> <i class="icon-minus-sign">\
		</i></a></td></tr>';

        $('#table1 > tbody').append(tmp);

        $('#close-' + node_tmp).click(function (event) {
            event.preventDefault();
            $('#row-' + node_tmp).fadeOut('slow', function () {
                $(this).remove();
            });
        });
    }

    $.ajax({
        type: 'GET',
        url: window.location.protocol + '//' + window.location.host + '/extractors/api/sample',
        dataType: 'json',
        success: function (data) {
            $.each(data, function (i, item) {
                addRowTable(parseInt(i), item.node, item.os);
            });

            if ($("#table1").find('tbody:first tr').length > 0) {
                getExtractors();
            }
        }
    });

    $('#table1').tablesorter({
        sortList: [
            [1, 0],
            [2, 0],
            [3, 0]
        ],
        onRenderHeader: function (index) {
            this.wrapInner('<span class="icons"></span>');
        }
    });

    $.ajax({
        type: 'GET',
        url: window.location.protocol + '//' + window.location.host + '/extractors/api/operatingsystems',
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            $.each(data.data, function (i, item) {
                $('#os-text').append($("<option></option>").attr("value", item).text(item));
            });
        }
    });

    function validate() {
        if ($('#business-cases-select option:selected').val() == "Business Case") {
            if ($('#business-cases-warning').length == 0) {
                $('#business-cases').addClass("control-group");
                $('#business-cases').addClass("error");
                $('#business-cases').append('<span id="business-cases-warning" class="help-inline">Please select your business case!</span>');
            }

            $(document.body).animate({
                'scrollTop':   $('#control-center').offset().top
            }, 500);

            console.log("Returning false...");

            return false;
        }

        var interrupt = true;
        var count = 0;

        $('#table1 > tbody > tr').each(function (i, item) {
            if ($(this).find('td > label > input:checkbox').is(':checked')) {
                count += 1;
                $(this).find('td:eq(4) ul li input:checkbox').each(function (i, item) {
                    if ($(this).is(':checked')) {
                        interrupt = false;
                    }
                });
            }
        });

        if (interrupt) {
            if ($('#warning-no-extractor').length == 0) {
                $('<div id="warning-no-extractor"></div>').insertBefore('#hidden-form');

                $('#warning-no-extractor').addClass("control-group");
                $('#warning-no-extractor').addClass("error");

                if (count > 0) {
                    var sentence = "";

                    if (count == 1) {
                        sentence = "You have one node selected but you forget (?) to choose at least one extractor.";
                    }

                    else {
                        sentence = "You have more than one node selected but you forget (?) to choose at least one extractor.";
                    }

                    $('#warning-no-extractor').append('<span id="business-cases-warning" class="help-inline">' + sentence + '</span>');
                }

                else {
                    $('#warning-no-extractor').append('<span id="business-cases-warning" class="help-inline">Please choose at least one remote node to capture information.</span>');
                }

                setTimeout(function () {
                    $('#warning-no-extractor').remove();
                }, 10000);
            }

            console.log("Returning false ...");

            return false;
        }

        $('#hidden-form input').remove();

        $('#hidden-form').append($('<input>').attr({
            type: 'hidden',
            name: 'business-cases-select',
            value: $('#business-cases-select option:selected').val()
        }));

        $('#table1 > tbody > tr').each(function (i, item) {
            var check = $(this).find('td > label > input:checkbox').is(':checked');

            if (check == true) {
                var node = $(this).find('td:eq(2)').text().trim();
                var os = $(this).find('td:eq(3)').text().trim();
                var extractors = $(this).find('td:eq(4)').text().replace('Available extractors', '').trim();

                $(this).find('td:eq(4) ul li input:checkbox').each(function (i, item) {
                    if ($(this).is(':checked')) {
                        $('#hidden-form').append($('<input>').attr({
                            type: 'hidden',
                            name: 'node-' + node,
                            value: node
                        }));

                        $('#hidden-form').append($('<input>').attr({
                            type: 'hidden',
                            name: 'os-' + node,
                            value: os
                        }));

                        $('#hidden-form').append($('<input>').attr({
                            type: 'hidden',
                            name: 'extractors-' + node + '[]',
                            value: $(this).parent().text().trim()
                        }));
                    }
                });
            }
        });

        console.log("Returning true ...");

        return true;
    }

    $('#extractors-submit-link').click(function (event) {
        event.preventDefault();

        var ret = validate();

        console.log("Validate1: " + ret);

        if (! ret) {
            console.log("Exiting !");
            return false;
        }

        var now = "";

        $(document.body).animate({
            'scrollTop':   $('#reports').offset().top
        }, 500);

        $.ajax({
            type: 'POST',
            url: window.location.protocol + '//' + window.location.host + '/extractors/api/done',
            data: $('#os-form').serialize(),
            beforeSend: function () {
                $("#myModalLabel").text("Status");
                $("#myModal > div:last").empty();
                $("#myModal").modal('show');

                if ($('#report-info').length == 0) {
                    console.log("Adding alert-info.");

                    $("#myModal > div:last").append('<div id="report-info" class="alert alert-info"><strong>Heads up!</strong> ' +
                        'Our highly trained team of goblins, have been dispatched to capture all the information you requested. ' +
                        'Please wait until they have finished their work. They will let you know when it\'s ready, don\'t worry!</div>');

                    now = moment().format();
                }
            }

        }).done(function () {
            $('#report-info').fadeOut('slow', function () {
                $(this).remove();
            });

            var elapsed = moment(now).fromNow();

            if ($('#report-success').length == 0) {
                console.log("Adding alert-success.");

                $("#myModal > div:last").append('<div id="report-success" class="alert alert-success"><strong>Extraction ' +
                'completed!</strong> See? I told you! They finished their work ' + elapsed + '.</div>');
            }
        });
    });
});