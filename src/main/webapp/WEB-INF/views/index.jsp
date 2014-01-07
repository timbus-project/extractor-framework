<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="author" content="Caixa Magica Software">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TIMBUS Capture Tool</title>

    <link href="<%= request.getContextPath() %>/resources/css/bootstrap.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/resources/css/bootstrap-responsive.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/resources/css/font-awesome.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/resources/theme/css/theme.default.css" rel="stylesheet">
    <!--[if IE 7]>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/font-awesome-ie7.min.css">
    <![endif]-->

    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!--[if gte IE 9]>
    <style>
        .ie_show { display:block }
        .ie_hide { display:none }
    </style>
    <![endif]-->
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.0.7/angular.min.js" type="text/javascript"></script>

    <script src="<%= request.getContextPath() %>/resources/js/angular/app.js" type="text/javascript"></script>
    <script src="<%= request.getContextPath() %>/resources/js/angular/maincontroller.js" type="text/javascript"></script>
</head>
<body id="top" data-spy="scroll" data-target=".navbar" style="padding-top: 40px;">
<div id='content' ng-app='TimbusExtractors' ng-controller='MainController'>
    <!-- Navbar
    ================================================== -->
    <div class="navbar navbar-fixed-top">
        <div class="navbar-inner" style="padding: 0 5% 0 5%;">
            <a class="brand" href="#top">
                <i class="icon-rocket"></i>
                <span style="color: #333333;"> TIMBUS</span> capture tool
            </a>
            <ul class="nav" style="font-size: 16px;">
                <li><a href="#control-center">Control Center</a></li>
                <li><a href="#reports">Reports</a></li>
            </ul>
            <ul class="nav pull-right" id="main-menu-right">
                <li>
                    <a id="documentation" rel="tooltip" href="/populator/api/reset" style="font-size: 20px;"><i class="icon-book"></i></a>
                </li>
            </ul>
        </div>
    </div>

    <div class="container" style="margin-top: 5%;">

        <!-- headline
        ================================================== -->
        <div class="hero-unit text-center">
            <img src="<%= request.getContextPath() %>/resources/img/logo3.png" class="img-rounded" />
        </div>

        <!-- dropdown
         ==================================================

       -->
        <div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h3 id="myModalLabel"></h3>
            </div>
            <div class="modal-body">
            </div>
        </div>

        <!-- Control Center
        ================================================== -->
        <div id="control-center" style="padding-top: 40px;" ng-controller='MainController'>
            <h2>Control Center</h2>
            <hr/>
            <div class="box">
                <div class="well" >
                    <div class="title" style="display: block; width: 100%; padding: 0; margin-bottom: 20px; font-size: 21px; line-height: 40px; color: #333333; border: 0; border-bottom: 1px solid #e5e5e5;">
                        <p><i class="icon-sitemap"></i> Remote Extracting</p>
                    </div>

                    <fieldset>
                        <label>Select a business case</label>
                        <div id="business-cases" class="form-inline">
                            <select id="business-cases-select">
                                <option selected disabled>Business Case</option>
                            </select>
                        </div>
                        <span class="help-block">&nbsp;</span>
                    </fieldset>

                    <fieldset>
                        <label>Insert node</label>
                        <div id="node-div" class="form-inline">
                            <input id="node-text" type="text" placeholder="127.0.0.1" ng-model="newMachine" ng-required="true">
                            <input id="node-text-port" type="text" placeholder="80" ng-model="newMachinePort">
                            <select id="os-text"
                                    ng-model="selectedModule"
                                    ng-options="extractor as (extractor.name+' '+extractor.version) for extractor in installedExtractors"
                                    ng-required="true">
                            </select>
<!--ng-options="value.name for value in installedExtractors"-->
                        </div>
                        <span class="help-block" style="margin-top: 10px">Insert a new node into the available remote node table.</span>
                        <button id="node-submit" type="submit" class="btn btn-success" ng-click="addNew()">Add</button>
                    </fieldset>

                    <fieldset>
                        <label>Filter</label>
                        <input type='text' ng-model='searchText' />
                        <label>List of available remote nodes</label>
                        <div id="table1-div" style="display: inline-block;">
                            <!--                 <table id="table1" class="tablesorter table-striped" style="float: left;">
                                              <thead>
                                               <tr>
                                                 <th data-sorter="false">&nbsp;</th>
                                                 <th>Id</th>
                                                 <th>Node</th>
                                                 <th>Operating System</th>
                                                 <th data-sorter="false">Extractors</th>
                                                 <th data-sorter="false">&nbsp;</th>
                                               </tr>
                                             </thead>
                                             <tbody>
                                             </tbody>
                                           </table> -->
                            <table ng-table class="table table-bordered table-striped" style="float: left;">
                                <thead>
                                <tr>
                                    <th>Id</th>
                                    <th>Node</th>
                                    <th>Port</th>
                                    <th>Operating System</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr ng-repeat="machine in machines | filter:searchText">
                                    <td title="Id">
                                        {{ $index + 1 }}
                                    </td>
                                    <td title="Hostname">
                                        {{machine.fqdn}}
                                    </td>
                                    <td title="Port">
                                        {{machine.port}}
                                    </td>
                                    <td title="Extractor">
                                        {{machine.module}}
                                    </td>
                                    <td>
                                        <button class="btn btn-danger" ng-click="removeMachine(machine)">Remove</button>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        <span class="help-block">Select which nodes and extractors you want to capture information.</span>
                    </fieldset>
                    <div id="hidden-form"></div>
                    <button id="extractors-submit-link" type="submit" class="btn btn-primary" ng-click="submitRequest()"><i class="icon-magic"></i> Extract</button>
                </div> <!-- well -->
            </div> <!-- box -->
        </div> <!-- control center -->


        <!-- Reports
        ================================================== -->
        <div id="reports" style="padding-top: 40px;">
            <h2>Reports</h2>
            <hr/>
            <div class="box">
                <div class="well">
                    <!-- <h1>Reports ...</h1> -->
                    <ul id="jobs" ng-model="jobs">
                        <li ng-repeat="job in jobs">{{job}}</li>
                    </ul>
<!--                     <div class="progress progress-striped active">
                        <div class="bar" style="width: 0%;"></div>
                    </div>
                    <div id="completed" class="alert alert-success" style="display: none;">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                    </div>
                    <div id="report"></div>
 -->                </div>
            </div>
        </div>


        <!-- API
        ================================================== -->
        <!--
              <div id="api" style="padding-top: 40px;">
                <h2>API</h2>
                <hr/>
                <div class="box">
                  <div class="well">
                    <h1>API ...</h1>
                  </div>
                </div>
              </div>
            -->

        <!-- Footer
        ================================================== -->
        <hr>
        <footer id="footer">
            <p class="pull-right">
                <a href="#top">Back to top</a>
            </p>
            <a href="http://www.caixamagica.pt">Caixa M&aacute;gica Software</a> &copy; 2013. All rights reserved.<br/>
            Code licensed under the <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache License v2.0</a>.<br/>
        </footer>

        <script src="<%= request.getContextPath() %>/resources/js/jquery-1.9.1.js"></script>
        <script src="<%= request.getContextPath() %>/resources/js/bootstrap.js"></script>
        <script src="<%= request.getContextPath() %>/resources/js/jquery.form.js"></script>
        <script src="<%= request.getContextPath() %>/resources/theme/js/jquery.tablesorter.js"></script>
        <script src="<%= request.getContextPath() %>/resources/theme/js/jquery.tablesorter.widgets.js"></script>
    </div>
</div>
</body>
</html>
