# customerService
End to end flow starts from placecustomerorder api
which will callsaga in sequential order
create order and create orderline
allocateinventory
makepayment
assigncarrier
createshipment
.
following managed servers added to run
AXon server. which orchestrate the event via event bus
this server can be started by dockercompose in managed server
api gateway is used to redirect all incoming request to validate request of authentication service
on successfull it redirects requests to each services
eureka registry is used to register auth service and for proxy redirects.
