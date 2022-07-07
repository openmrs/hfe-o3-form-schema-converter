<%
    ui.decorateWith("kenyaemr", "standardPage", [layout: "sidebar"])
    def menuItems = [
            [label: "Back", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: ui.pageLink("kenyaemr", "userHome")]
    ]

    ui.includeJavascript("htmltojson", "jquery.twbsPagination.min.js")
    ui.includeJavascript("htmltojson", "jsonViewer/jquery.json-editor.min.js")

    ui.includeJavascript("htmltojson", "bootstrap/bootstrap.bundle.min.js")
    ui.includeCss("htmltojson", "bootstrap/bootstrap-iso.css")
    ui.includeCss("htmltojson", "jsonViewer/jquery.json-viewer.css")
%>
<style>
.simple-table {
    border: solid 1px #DDEEEE;
    border-collapse: collapse;
    border-spacing: 0;
    font: normal 13px Arial, sans-serif;
}
.simple-table thead th {

    border: solid 1px #DDEEEE;
    color: #336B6B;
    padding: 10px;
    text-align: left;
    text-shadow: 1px 1px 1px #fff;
}
.simple-table td {
    border: solid 1px #DDEEEE;
    color: #333;
    padding: 5px;
    text-shadow: 1px 1px 1px #fff;
}
table {
    width: 95%;
}
th, td {
    padding: 5px;
    text-align: left;
    height: 30px;
    border-bottom: 1px solid #ddd;
}
tr:nth-child(even) {background-color: #f2f2f2;}
#pager li{
    display: inline-block;
}

#queue-pager li{
    display: inline-block;
}

.formName {
    width: 200px;
}

.formUuid {
    width: 200px;
}

.actionColumn {
    width: 200px;
}

.dataPoints {
    width: 100px;
}

.pagination-sm .page-link {
    padding: .25rem .5rem;
    font-size: .875rem;
}
.page-link {
    position: relative;
    display: block;
    padding: .5rem .75rem;
    margin-left: -1px;
    line-height: 1.25;
    color: #0275d8;
    background-color: #fff;
    border: 1px solid #ddd;
}

.viewPayloadButton {
    background-color: cadetblue;
    color: white;
    margin-right: 5px;
    margin-left: 5px;
}

.writeFormSchemaButton {
    background-color: cadetblue;
    color: white;
    margin-right: 5px;
    margin-left: 5px;
}

.writeFormSchemaButton:hover {
    background-color: orange;
    color: black;
}
.viewPayloadButton:hover {
    background-color: orange;
    color: black;
}
.editPayloadButton {
    background-color: cadetblue;
    color: white;
    margin-right: 5px;
    margin-left: 5px;
}
.editPayloadButton:hover {
    background-color: orange;
    color: black;
}

.viewButton:hover {
    background-color: steelblue;
    color: white;
}

.page-content{
    background: #eee;
    display: inline-block;
    padding: 10px;
    max-width: 660px;
    font-weight: bold;
}
@media screen and (min-width: 676px) {
    .modal-dialog {
        max-width: 600px; /* New width for default modal */
    }
}
</style>

<div class="ke-page-sidebar">
    ${ui.includeFragment("kenyaui", "widget/panelMenu", [heading: "Back", items: menuItems])}
</div>

<div class="ke-page-content">

    <div>
        <fieldset>
            <legend>HTML Forms summary</legend>
            <div>
                <table class="simple-table" width="100%">
                    <thead>
                    </thead>
                    <tbody>
                    <tr>
                        <td width="15%">Total forms</td>
                        <td>${hfeFormsSize}</td>
                    </tr>

                    <tr>
                        <td colspan="2"><button class="writeFormSchemaButton" type="button" id="generateForms">Export HFE forms schema</button> <span id="formGenerationOutcome"></span> </td>
                    </tr>

                    </tbody>
                </table>
            </div>
        </fieldset>
    </div>

    <div id="program-tabs" class="ke-tabs">

        <div class="ke-tabmenu">
            <div class="ke-tabmenu-item" data-tabid="forms-list">HTML forms</div>
        </div>

        <div class="ke-tab" data-tabid="forms-list">
            <table id="general-error-queue-data" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td style="width: 99%; vertical-align: top">
                        <div class="ke-panel-frame">
                            <div class="ke-panel-heading">HTML Forms</div>

                            <div class="ke-panel-content">
                                    <fieldset>
                                        <legend></legend>
                                        <table class="simple-table" width="100%">
                                            <thead>

                                            <tr>
                                                <th class="formUuid">UUID</th>
                                                <th class="formName">Name</th>
                                                <th class="dataPoints">No of Data points (obs, obsgroup)</th>

                                                <th class="actionColumn">Json Schema</th>
                                            </tr>
                                            </thead>
                                            <tbody id="general-error-list">

                                            </tbody>

                                        </table>

                                        <div id="pager">
                                            <ul id="generalErrorPagination" class="pagination-sm"></ul>
                                        </div>
                                    </fieldset>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div class="bootstrap-iso">

        <div class="modal fade" id="showViewPayloadDialog" tabindex="-1" role="dialog" aria-labelledby="backdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
                <div class="modal-content">
                    <div class="modal-header modal-header-primary">
                        <h5 class="modal-title" id="backdropLabel">View Payload</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <span style="color: firebrick" id="msgBox"></span>
                        <pre id="json-view-display"></pre>
                    </div>
                    <div class="modal-footer modal-footer-primary">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="showEditPayloadDialog" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" role="dialog" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
                <div class="modal-content">
                    <div class="modal-header modal-header-primary">
                        <h5 class="modal-title" id="staticBackdropLabel">Edit Payload</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <span style="color: firebrick" id="msgBox"></span>
                        <pre id="json-edit-display"></pre>
                    </div>
                    <div class="modal-footer modal-footer-primary">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button>Download JSON</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="showConfirmationBox" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" role="dialog" aria-labelledby="staticConfirmLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-sm" role="document">
                <div class="modal-content">
                    <div class="modal-header modal-header-primary">
                        <h5 class="modal-title" id="staticConfirmLabel">Please Confirm</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <span style="color: firebrick" id="msgBox"></span>
                        <pre id="json-confirm-display"></pre>
                    </div>
                    <div class="modal-footer modal-footer-primary">
                        <button type="button" class="confirmNoButton btn btn-secondary" data-bs-dismiss="modal">No</button>
                        <button type="button" class="confirmYesButton btn btn-primary">Yes</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal" id="showWaitBox" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" role="dialog" aria-labelledby="staticConfirmLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-sm" role="document">
                <div class="modal-content">
                    <div class="modal-header modal-header-primary">
                        <h5 class="modal-title" id="staticConfirmLabel">Please Wait</h5>
                    </div>
                    <div class="modal-body">
                        <div>
                            <span style="padding:2px; display:inline-block;"> <img src="${ui.resourceLink("afyastat", "images/loading.gif")}" /> </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal" id="showInfoBox" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" role="dialog" aria-labelledby="staticInfoLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-sm" role="document">
                <div class="modal-content">
                    <div class="modal-header modal-header-primary">
                        <h5 class="modal-title" id="staticInfoLabel">Info</h5>
                    </div>
                    <div class="modal-body">
                        <span style="color: firebrick" id="msgBox"></span>
                        <pre id="json-info-display"></pre>
                    </div>
                    <div class="modal-footer modal-footer-primary">
                        <button type="button" class="confirmOkButton btn btn-secondary" data-bs-dismiss="modal">OK</button>
                    </div>
                </div>
            </div>
        </div>

    </div>

</div>

<script type="text/javascript">

    //On ready
    jq = jQuery;
    jq(function () {
        // apply pagination

        var hfeFormsPaginationDiv = jq('#generalErrorPagination');
        var hfeFormsListDisplayArea = jq('#general-error-list');
        var numberOfHfeForms = ${ hfeFormsSize };
        var hfeFormsList = ${ hfeForms };
        var hfeDataDisplayRecords = [];
        var recPerPage = 10;
        var hfeFormsStartPage = 1;
        var totalHfeFormsPages = Math.ceil(numberOfHfeForms / recPerPage);
        var visibleHfeFormsPages = 1;
        var payloadEditor = {};

        if (totalHfeFormsPages <= 5) {
            visibleHfeFormsPages = totalHfeFormsPages;
        } else {
            visibleHfeFormsPages = 5;
        }

        if (numberOfHfeForms > 0) {
            apply_pagination(hfeFormsPaginationDiv, hfeFormsListDisplayArea, totalHfeFormsPages, visibleHfeFormsPages, hfeFormsList, hfeDataDisplayRecords, 'general-error', hfeFormsStartPage); // general records in error
        }
        function apply_pagination(paginationDiv, recordsDisplayArea, totalPages, visiblePages, allRecords, recordsToDisplay, tableId, page) {
            paginationDiv.twbsPagination({
                totalPages: totalPages,
                visiblePages: visiblePages,
                onPageClick: function (event, page) {
                    displayRecordsIndex = Math.max(page - 1, 0) * recPerPage;
                    endRec = (displayRecordsIndex) + recPerPage;

                    recordsToDisplay = allRecords.slice(displayRecordsIndex, endRec);
                    generate_table(recordsToDisplay, recordsDisplayArea, tableId);
                }
            });
        }

        function AsyncConfirmYesNo(title, msg, yesFn, noFn) {
            jq("#staticConfirmLabel").html(title);
            jq("#json-confirm-display").html(msg);
            jq(".confirmYesButton").off('click').click(function () {
                yesFn();
                jq('#showConfirmationBox').modal("hide");
            });
            jq(".confirmNoButton").off('click').click(function () {
                noFn();
                jq('#showConfirmationBox').modal("hide");
            });
            jq('#showConfirmationBox').modal('show');
        }

        function AsyncShowInfo(title, msg, okFn) {
            jq("#staticInfoLabel").html(title);
            jq("#json-info-display").html(msg);
            jq(".confirmOkButton").off('click').click(function () {
                okFn();
                jq('#showInfoBox').modal("hide");
            });
            jq('#showInfoBox').modal('show');
        }

        function reloadPage() {
            document.location.reload();
        }

        jq(document).on('click','.viewPayloadButton',function () {
            var formUuid = jq(this).val();
            console.log("Checking form with uuid: " + formUuid);

            ui.getFragmentActionAsJson('afyastat', 'htmlFormToJsonSchema', 'getFormSchema', { formUuid : formUuid }, function (result) {
                let payloadObject = [];
                try {
                    payloadObject = JSON.parse(result.payload);
                } catch(ex) {
                    payloadObject = JSON.parse("{}")
                }
                
                jq('#json-view-display').empty();
                jq('#json-view-display').jsonViewer(payloadObject,{
                    withQuotes:true,
                    rootCollapsable:true
                });
            });

            jq('#showViewPayloadDialog').modal('show');
        });

        jq(document).on('click','.editPayloadButton',function () {
            var formUuid = jq(this).val();
            console.log("Checking form with uuid: " + formUuid);

            ui.getFragmentActionAsJson('afyastat', 'htmlFormToJsonSchema', 'getFormSchema', { formUuid : formUuid }, function (result) {
                let payloadObject = [];
                try {
                    payloadObject = JSON.parse(result.payload);
                } catch(ex) {
                    payloadObject = JSON.parse("{}")
                }

                jq('#json-edit-display').empty();
                payloadEditor = new JsonEditor('#json-edit-display', payloadObject,{
                    withQuotes:true,
                    rootCollapsable:true
                });
            });

            jq('#showEditPayloadDialog').modal('show');
        });

        jq(document).on('click','#generateForms',function () {
            jq('#formGenerationOutcome').text('');
            ui.getFragmentActionAsJson('afyastat', 'htmlFormToJsonSchema', 'generateForms', function (result) {
                var status = result.success === true ? 'HFE schema exported successfully' : 'There was a problem copying html forms. Please check logs for more information';

                jq('#formGenerationOutcome').text(status);
                console.log(result);
            });
        });
    });

    function generate_table(displayRecords, displayObject, tableId) {
        var tr;
        displayObject.html('');
        for (var i = 0; i < displayRecords.length; i++) {

            tr = jq('<tr/>');
            tr.append("<td>" + displayRecords[i].uuid + "</td>");
            tr.append("<td>" + displayRecords[i].formName + "</td>");

            tr.append("<td>" + displayRecords[i].dataPoints + "</td>");

            var actionTd = jq('<td/>');

            var btnView = jq('<button/>', {
                text: 'View',
                class: 'viewPayloadButton',
                value: displayRecords[i].uuid
            });

            var btnEdit = jq('<button/>', {
                text: 'View and copy',
                class: 'editPayloadButton',
                value: displayRecords[i].uuid
            });

            actionTd.append(btnView);
            actionTd.append(btnEdit);

            tr.append(actionTd);

            displayObject.append(tr);
        }
    }


</script>