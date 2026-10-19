const $ = (s) => document.querySelector(s);
let currentRole = 'GUEST';
const customerItems = [
  ['dashboard','⌂','Dashboard'],['packages','▣','Travel Packages'],['hotels','▥','Hotels'],['destinations','⌖','Destinations'],['book','✈','Book a Trip'],['bookings','▦','My Bookings']
];
const adminItems = [
  ['dashboard','⌂','Dashboard'],['packages','▣','Manage Packages'],['hotels','▥','Manage Hotels'],['destinations','⌖','Manage Destinations'],['routes','▰','Manage Travel Routes'],['bookings','▦','Manage Bookings'],['users','♟','Manage Users']
];
const cards = [
  ['packages','✈','Travel Packages','Explore curated trips, compare prices and discover your next destination.','../images/02_travel_packages.png'],
  ['hotels','▥','Hotels','Find comfortable stays with location, price and rating details.','../images/03_hotels.png'],
  ['destinations','⌖','Destinations','Explore beautiful places, local culture and unforgettable experiences.','../images/04_destinations.png'],
  ['book','➜','Book a Trip','Build your itinerary and continue through the existing booking flow.','../images/05_book_trip.png'],
  ['bookings','▦','My Bookings','Review your bookings, open tickets and manage reservations.','../images/06_my_bookings.png'],
  ['bookings','🎫','Tickets & Receipts','Open your saved reservations to view the corresponding ticket or receipt.','../images/08_ticket.png']
];
function bridge(){ return window.travelBridge || null; }
function toast(message){const el=$('#toast');el.textContent=message;el.classList.add('show');setTimeout(()=>el.classList.remove('show'),3200)}
function toggleSidebar(){ $('#sidebar').classList.toggle('open'); }
function route(key){if(key==='dashboard'){renderDashboard(bridge()?.currentName?.()||'Traveler',bridge()?.currentRole?.()||currentRole);return;}if(bridge())bridge().openPage(key);else toast('Desktop bridge is not available. Restart the desktop application.');}
function renderNav(role){const isAdmin=String(role).toUpperCase()==='ADMIN';const items=isAdmin?adminItems:customerItems;$('#navLinks').innerHTML=items.map(([key,icon,label])=>`<button class="nav-item ${key==='dashboard'?'active':''}" data-key="${key}" onclick="route('${key}')"><span class="nav-icon">${icon}</span><span>${label}</span></button>`).join('');$('#profileRole').textContent=isAdmin?'Administrator':(String(role).toLowerCase()==='guest'?'Guest':'Customer');}
function renderLogin(){currentRole='GUEST';document.querySelector('.app-shell')?.classList.add('login-mode');$('#profileName').textContent='Traveler';$('#navLinks').innerHTML='';$('#pageTitle').textContent='Your next journey starts here';$('#content').innerHTML=`<div class="login-layout"><section class="login-art"><div><span class="eyebrow">TRAVEL MORE · WORRY LESS</span><h2>Collect moments,<br>not things.</h2><p>Plan your next escape with a thoughtfully designed travel experience. Explore destinations, find your stay and book with confidence.</p></div></section><section class="auth-card"><span class="eyebrow">WELCOME BACK</span><h2>Sign in to travel</h2><p>Enter your account details to continue to your personalized travel workspace.</p><form onsubmit="submitLogin(event)"><div class="field"><label for="username">Username</label><input id="username" autocomplete="username" required placeholder="Enter your username"></div><div class="field"><label for="password">Password</label><input id="password" type="password" autocomplete="current-password" required placeholder="Enter your password"></div><button class="btn btn-primary" type="submit">Sign in securely&nbsp; →</button></form><div class="auth-foot">New to Travel & Tourism? <button onclick="openRegister()">Create an account</button></div><div class="notice">Your existing Java authentication service and MySQL user records are used to verify your login.</div></section></div>`;}
function submitLogin(e){e.preventDefault();const b=bridge();if(!b){toast('Desktop bridge is not available.');return;}b.login($('#username').value.trim(),$('#password').value);}
function loginResult(ok,message){if(!ok){toast(message||'Login failed.');return;}currentRole=message;renderDashboard(bridge()?.currentName?.()||'Traveler',message);toast('Signed in successfully.');}
function openRegister(){if(bridge())bridge().openRegister();else toast('Registration is available in the desktop application.');}
function renderDashboard(name='Traveler',role='CUSTOMER'){document.querySelector('.app-shell')?.classList.remove('login-mode');currentRole=role||'CUSTOMER';const isAdmin=String(currentRole).toUpperCase()==='ADMIN';$('#profileName').textContent=name||'Traveler';renderNav(currentRole);$('#pageTitle').textContent=isAdmin?'Admin workspace':'Your next journey starts here';const visibleCards=isAdmin?[
 ['packages','▣','Manage Packages','Create and maintain tour packages, pricing and availability.','../images/10_admin_packages.png'],['hotels','▥','Manage Hotels','Manage hotel details, room availability and pricing.','../images/11_admin_hotels.png'],['destinations','⌖','Manage Destinations','Maintain destination information and imagery.','../images/12_admin_destinations.png'],['routes','▰','Manage Travel Routes','Maintain bus, train and flight route schedules.','../images/13_admin_routes.png'],['bookings','▦','Manage Bookings','Review reservations and manage booking status.','../images/14_admin_bookings.png'],['users','♟','Manage Users','Manage customer and administrator accounts.','../images/15_admin_users.png']]:cards;
$('#content').innerHTML=`<section class="hero"><div class="hero-copy"><span class="kicker">${isAdmin?'ADMINISTRATION · CONTROL CENTER':'EXPLORE · PLAN · BOOK · TRAVEL'}</span><h2>${isAdmin?'Everything in one place.':'Find your next great escape.'}</h2><p>${isAdmin?'Manage travel inventory, routes, bookings and user access from one consistent workspace.':'From mountain retreats to coastal escapes, start planning a journey that feels like yours.'}</p><div class="hero-actions"><button class="btn btn-primary" onclick="route('${isAdmin?'packages':'packages'}')">${isAdmin?'Manage travel inventory':'Explore packages'} &nbsp;→</button><button class="btn btn-light" onclick="route('${isAdmin?'bookings':'book'}')">${isAdmin?'View bookings':'Plan a trip'}</button></div></div></section><div class="section-head"><div><h2>${isAdmin?'Management modules':'Explore your travel options'}</h2><p>${isAdmin?'Choose a module to open its existing Java-backed workflow.':'Everything you need to plan, book and manage your journey.'}</p></div></div><section class="cards">${visibleCards.map(([key,icon,title,desc,img])=>`<article class="feature-card" role="button" tabindex="0" onclick="route('${key}')" onkeydown="if(event.key==='Enter')route('${key}')" style="--card-image:url('${img}')"><div class="feature-inner"><div class="feature-icon">${icon}</div><h3>${title}</h3><p>${desc}</p><span class="card-link">OPEN MODULE &nbsp;↗</span></div></article>`).join('')}</section><section class="quick-stats"><div class="stat"><span class="label">APPLICATION</span><strong>Desktop</strong><div class="hint">Embedded web interface</div></div><div class="stat"><span class="label">DATA LAYER</span><strong>MySQL</strong><div class="hint">Existing JDBC connection</div></div><div class="stat"><span class="label">SIGNED-IN ROLE</span><strong>${isAdmin?'Administrator':'Traveler'}</strong><div class="hint">Role-based module access</div></div></section><div class="notice">The modern HTML interface is embedded in the desktop window. Module buttons continue to open the existing Java screens so current DAO, service, booking, payment and ticket workflows are not silently replaced by mock web forms.</div>`;}
function renderModule(key,name='Traveler',role='CUSTOMER'){
 currentRole=role||currentRole; const isAdmin=String(currentRole).toUpperCase()==='ADMIN';
 const titles={packages:isAdmin?'Manage Travel Packages':'Travel Packages',hotels:isAdmin?'Manage Hotels':'Hotels',destinations:isAdmin?'Manage Destinations':'Destinations',routes:'Manage Travel Routes',users:'Manage Users',bookings:'Manage Bookings'};
 const config={packages:{eyebrow:'DISCOVER YOUR NEXT JOURNEY',desc:'Explore packages from the connected database.',icon:'✈',price:'Package price'},hotels:{eyebrow:'STAY COMFORTABLY',desc:'Explore hotel options from the connected database.',icon:'▥',price:'Price per night'},destinations:{eyebrow:'FIND YOUR NEXT DESTINATION',desc:'Discover destinations saved in your database.',icon:'⌖',price:''},routes:{eyebrow:'TRANSPORT INVENTORY',desc:'Bus, train and flight routes currently stored in the database.',icon:'▰'},users:{eyebrow:'ACCOUNT ADMINISTRATION',desc:'Manage account visibility and review user records.',icon:'♟'},bookings:{eyebrow:'RESERVATION CONTROL',desc:'Review booking records from the connected database.',icon:'▦'}}[key];
 if(!config){toast('This screen is not available.');return;}
 if((['routes','users'].includes(key)||key==='bookings')&&!isAdmin){toast('This page is available to administrators only.');return;}
 document.querySelectorAll('.nav-item').forEach(el=>el.classList.toggle('active',el.dataset.key===key));
 $('#pageTitle').textContent=titles[key];
 $('#content').innerHTML=`<section class="module-hero"><div><span class="kicker">${config.eyebrow}</span><h2>${titles[key]}</h2><p>${config.desc}</p></div><button class="btn btn-light" onclick="route('dashboard')">← Dashboard</button></section><div class="module-toolbar"><div><h2>${isAdmin?'Manage':'Available'} ${key}</h2><p>Connected Java backend · MySQL records</p></div><input id="moduleSearch" class="module-search" placeholder="Search ${key}..." oninput="filterModuleCards()"><button class="btn btn-primary" onclick="renderModule('${key}',bridge()?.currentName?.()||'Traveler',bridge()?.currentRole?.()||currentRole)">Refresh</button></div><div id="moduleResults" class="data-cards"><div class="module-loading">Loading ${key}…</div></div><div class="notice">This screen uses the shared HTML/CSS interface. The displayed records are retrieved from the Java backend. Administrative changes should be made only through implemented backend actions.</div>`;
 try{
  const raw=(['routes','users'].includes(key)||(key==='bookings'&&isAdmin))?bridge()?.getAdminModuleData(key):bridge()?.getModuleData(key);
  const data=JSON.parse(raw||'[]');
  if(!Array.isArray(data)){ $('#moduleResults').innerHTML=`<div class="module-empty">Could not load records: ${escapeHtml(data.error||'Unknown error')}</div>`;return;}
  window.moduleRows=data;window.moduleKey=key;
  if(isAdmin && ['packages','hotels','destinations'].includes(key)){renderAdminCrudModule(data,key);}
  else if(['routes','users','bookings'].includes(key)){renderGenericTable(data,key,isAdmin);}
  else renderModuleCards(data,key,{...config,title:titles[key],availability:key==='hotels'?'rooms':'seats'});
 }catch(e){$('#moduleResults').innerHTML='<div class="module-empty">Unable to load records. Check the database connection.</div>';}
}
function renderAdminCrudModule(rows,key){
 const labels={
  packages:[['name','Package name'],['location','Destination'],['duration','Duration (days)'],['price','Price'],['availability','Available seats']],
  hotels:[['name','Hotel name'],['location','Location'],['price','Price per night'],['availability','Available rooms'],['rating','Rating']],
  destinations:[['name','Destination name'],['location','Location / State'],['description','Description'],['famous','Famous for']]
 }[key];
 const formFields=labels.map(([id,label])=>`<div class="field"><label for="crud_${id}">${label}</label><input id="crud_${id}" name="${id}" required placeholder="${label}"></div>`).join('');
 $('#moduleResults').className='admin-layout'; $('#moduleResults').innerHTML=`<form class="trip-form admin-crud-form" onsubmit="saveAdminForm(event,'${key}')"><div class="field"><label for="crud_id">Record ID (blank = add new)</label><input id="crud_id" placeholder="Select a table row to update"></div>${formFields}<div class="trip-actions"><button class="btn btn-primary" type="submit">Save record</button><button class="btn btn-light" type="button" onclick="clearAdminForm()">Clear form</button></div><div id="crudMessage" class="notice">Select an existing row below to load it into the form. Delete operations may be blocked when related bookings exist.</div></form><div class="table-wrap admin-crud-table"><table class="data-table"><thead><tr><th>ID</th>${labels.map(x=>`<th>${escapeHtml(x[1])}</th>`).join('')}<th>Action</th></tr></thead><tbody>${rows.map(r=>`<tr>${`<td>${r.id}</td>`}${labels.map(([id])=>`<td>${escapeHtml(r[id]??(id==='duration'?(String(r.detail||'').match(/\d+/)||[''])[0]:id==='rating'?(String(r.detail||'').match(/[\d.]+/)||[''])[0]:id==='description'?r.detail:'—'))}</td>`).join('')}<td><button class="btn btn-light" type="button" onclick="selectAdminRow('${key}',${Number(r.id)})">Edit</button> <button class="btn btn-danger" type="button" onclick="deleteAdminRow('${key}',${Number(r.id)})">Delete</button></td></tr>`).join('')}</tbody></table></div>`;
 window.moduleRows=rows;window.moduleKey=key;
}
function selectAdminRow(key,id){
 const r=(window.moduleRows||[]).find(x=>Number(x.id)===Number(id));if(!r)return;
 $('#crud_id').value=r.id;
 const set=(id,v)=>{const el=$('#crud_'+id);if(el)el.value=v??'';};
 set('name',r.name);set('location',r.location);
 if(key==='packages'){set('duration',r.duration??(String(r.detail||'').match(/\d+/)?.[0]||''));set('price',r.price);set('availability',r.availability);}
 if(key==='hotels'){set('price',r.price);set('availability',r.availability);set('rating',String(r.detail||'').match(/[\d.]+/)?.[0]||'');}
 if(key==='destinations'){set('description',r.description??r.detail);set('famous',r.famous||'');}
 $('#crudMessage').textContent='Editing record #'+id+'. Save to apply changes.';
}
function clearAdminForm(){const f=document.querySelector('.admin-crud-form');if(f)f.reset();const msg=$('#crudMessage');if(msg)msg.textContent='Form cleared.';}
function saveAdminForm(e,key){
 e.preventDefault();
 const val=id=>$('#crud_'+id)?.value.trim()||'';
 const payload=key==='packages'?[val('name'),val('location'),val('duration'),val('price'),val('availability')]:
 key==='hotels'?[val('name'),val('location'),val('price'),val('availability'),val('rating')]:
 [val('name'),val('location'),val('description'),val('famous'),''];
 try{
  const raw=bridge()?.saveAdminRecord(key,val('id'),...payload);const r=JSON.parse(raw||'{}');
  $('#crudMessage').textContent=r.message||'No response from backend.';
  if(r.ok){toast(r.message);renderModule(key,bridge()?.currentName?.()||'Administrator','ADMIN');}
 }catch(err){$('#crudMessage').textContent='Could not save this record. Check the database and form values.';}
}
function deleteAdminRow(key,id){
 if(!confirm('Delete record #'+id+'? Existing bookings may prevent deletion.'))return;
 try{const r=JSON.parse(bridge()?.deleteAdminRecord(key,String(id))||'{}');toast(r.message||'Delete failed.');if(r.ok)renderModule(key,bridge()?.currentName?.()||'Administrator','ADMIN');}
 catch(e){toast('Delete failed. Check database relationships.');}
}
function renderGenericTable(rows,key,isAdmin){
 const target=$('#moduleResults');
 if(key==='routes' && isAdmin){renderAdminRoutes(rows);return;}
 target.className='full-table-layout';
 if(!rows.length){target.innerHTML='<div class="module-empty">No records found.</div>';return;}
 const columns=Object.keys(rows[0]);
 target.innerHTML=`<div class="table-wrap"><table class="data-table"><thead><tr>${columns.map(c=>`<th>${escapeHtml(c.replace(/([A-Z])/g,' $1'))}</th>`).join('')}${key==='bookings'&&isAdmin?'<th>Action</th>':''}</tr></thead><tbody>${rows.map(r=>`<tr>${columns.map(c=>`<td>${escapeHtml(r[c]??'—')}</td>`).join('')}${key==='bookings'&&isAdmin?`<td>${String(r.status||'').toUpperCase()==='CANCELLED'?'—':`<button class="btn btn-danger" onclick="cancelAdminBooking(${Number(r.id)})">Cancel Booking</button>`}</td>`:''}</tr>`).join('')}</tbody></table></div>`;
}
function cancelAdminBooking(id){
 if(!confirm('Cancel booking #'+id+' for the customer? Inventory reserved by this booking will be restored.'))return;
 try{const ok=bridge()?.cancelAdminBooking(Number(id));if(ok){toast('Booking #'+id+' cancelled; inventory restored.');renderModule('bookings',bridge()?.currentName?.()||'Administrator','ADMIN');}else toast('Cancellation failed. Check the database and booking status.');}
 catch(e){toast('Cancellation failed: '+(e?.message||'Please retry.'));}
}
function renderAdminRoutes(rows){
 const target=$('#moduleResults'); target.className='admin-layout routes-admin-layout';
 const fields=[['type','Type (BUS/TRAIN/FLIGHT)'],['operator','Operator'],['number','Route number'],['departure','Departure'],['destination','Destination'],['distance','Distance (km)'],['departureTime','Departure time (HH:MM:SS)'],['arrivalTime','Arrival time (HH:MM:SS)'],['fare','Fare'],['seats','Available seats']];
 target.innerHTML=`<form class="trip-form admin-crud-form route-form" onsubmit="saveAdminRouteForm(event)"><h3>Add / Update Travel Route</h3><div class="route-fields"><div class="field"><label>Record ID (blank = add)</label><input id="route_id" placeholder="Select a route to edit"></div>${fields.map(([id,label])=>`<div class="field"><label for="route_${id}">${label}</label><input id="route_${id}" ${['type','operator','number','departure','destination','distance','departureTime','arrivalTime','fare','seats'].includes(id)?'required':''} placeholder="${label}"></div>`).join('')}</div><div class="trip-actions"><button class="btn btn-primary" type="submit">Save route</button><button class="btn btn-light" type="button" onclick="clearRouteForm()">Clear form</button></div><div id="routeMessage" class="notice">Select a route in the table to edit it, or fill the form to add one.</div></form><section class="route-table-section"><h3>All travel routes</h3><div class="table-wrap"><table class="data-table"><thead><tr><th>ID</th><th>Type</th><th>Operator</th><th>Number</th><th>Departure</th><th>Destination</th><th>Distance</th><th>Departure time</th><th>Arrival time</th><th>Fare</th><th>Seats</th><th>Actions</th></tr></thead><tbody>${rows.map(r=>`<tr><td>${Number(r.id)}</td><td>${escapeHtml(r.type||'')}</td><td>${escapeHtml(r.operator||'')}</td><td>${escapeHtml(r.number||'')}</td><td>${escapeHtml(r.departure||'')}</td><td>${escapeHtml(r.destination||'')}</td><td>${escapeHtml(r.distance??'')}</td><td>${escapeHtml(r.departureTime||'')}</td><td>${escapeHtml(r.arrivalTime||'')}</td><td>${escapeHtml(r.fare??'')}</td><td>${escapeHtml(r.seats??'')}</td><td><button class="btn btn-light" onclick="selectAdminRoute(${Number(r.id)})">Edit</button> <button class="btn btn-danger" onclick="deleteAdminRoute(${Number(r.id)})">Delete</button></td></tr>`).join('')}</tbody></table></div></section>`;
 window.moduleRows=rows;window.moduleKey='routes';
}
function selectAdminRoute(id){
 const r=(window.moduleRows||[]).find(x=>Number(x.id)===Number(id));if(!r)return;
 $('#route_id').value=r.id;
 for(const k of ['type','operator','number','departure','destination','distance','departureTime','arrivalTime','fare','seats']){const el=$('#route_'+k);if(el)el.value=r[k]??'';}
 $('#routeMessage').textContent='Editing route #'+id+'. Save to apply changes.';
}
function clearRouteForm(){document.querySelector('.route-form')?.reset();$('#routeMessage').textContent='Form cleared.';}
function saveAdminRouteForm(e){
 e.preventDefault(); const v=id=>$('#route_'+id)?.value.trim()||'';
 try{const raw=bridge()?.saveAdminRoute(v('id'),v('type').toUpperCase(),v('operator'),v('number'),v('departure'),v('destination'),v('distance'),v('departureTime'),v('arrivalTime'),v('fare'),v('seats'));const r=JSON.parse(raw||'{}');$('#routeMessage').textContent=r.message||'No response';toast(r.message||'Save failed');if(r.ok)renderModule('routes',bridge()?.currentName?.()||'Administrator','ADMIN');}
 catch(err){$('#routeMessage').textContent='Could not save route. Verify values and database schema.';}
}
function deleteAdminRoute(id){
 if(!confirm('Delete travel route #'+id+'? Existing bookings may prevent deletion.'))return;
 try{const r=JSON.parse(bridge()?.deleteAdminRoute(String(id))||'{}');toast(r.message||'Delete failed');if(r.ok)renderModule('routes',bridge()?.currentName?.()||'Administrator','ADMIN');}
 catch(e){toast('Delete failed. Check related bookings.');}
}
function renderBookTrip(name='Traveler',role='CUSTOMER'){
 currentRole=role||'CUSTOMER';$('#profileName').textContent=name||'Traveler';renderNav(currentRole);
 document.querySelectorAll('.nav-item').forEach(el=>el.classList.toggle('active',el.dataset.key==='book'));
 $('#pageTitle').textContent='Book a Trip';
 $('#content').innerHTML=`<section class="module-hero"><div><span class="kicker">PLAN YOUR NEXT JOURNEY</span><h2>Book a Trip</h2><p>Choose your package, hotel and passenger count. The Java booking service validates availability and calculates the final amount.</p></div><button class="btn btn-light" onclick="route('dashboard')">← Dashboard</button></section><form id="tripForm" class="trip-form" onsubmit="submitTrip(event)"><div class="field"><label for="tripPackage">Travel package</label><select id="tripPackage" required></select></div><div class="field"><label for="tripHotel">Hotel</label><select id="tripHotel" required></select></div><div class="field"><label for="tripTransport">Transport (optional)</label><select id="tripTransport"><option value="0">No additional transport</option></select></div><div class="field"><label for="tripPeople">Number of passengers</label><input id="tripPeople" type="number" min="1" max="20" value="1" required></div><div class="trip-actions"><button class="btn btn-primary" type="submit">Create Booking →</button><button class="btn btn-light" type="button" onclick="route('bookings')">My Bookings</button></div><div id="tripResult" class="notice">Prices and availability are verified by the Java service.</div></form>`;
 try{
  const packages=JSON.parse(bridge()?.getModuleData('packages')||'[]'), hotels=JSON.parse(bridge()?.getModuleData('hotels')||'[]'), routes=JSON.parse(bridge()?.getTransportOptions?.()||'[]');
  $('#tripPackage').innerHTML=packages.map(x=>`<option value="${x.id}">${escapeHtml(x.name)} — ₹${Number(x.price).toLocaleString('en-IN')}</option>`).join('');
  $('#tripHotel').innerHTML=hotels.map(x=>`<option value="${x.id}">${escapeHtml(x.name)} — ₹${Number(x.price).toLocaleString('en-IN')}/night</option>`).join('');
  $('#tripTransport').innerHTML+='<option value="0">No additional transport</option>'+routes.map(x=>`<option value="${x.id}">${escapeHtml(x.type)} · ${escapeHtml(x.operator)} · ${escapeHtml(x.departure)} → ${escapeHtml(x.destination)} · ₹${Number(x.fare||0)}</option>`).join('');
 }catch(e){$('#tripResult').textContent='Could not load travel options. Check the database connection.';}
}
function submitTrip(e){
 e.preventDefault();const result=$('#tripResult');result.textContent='Creating booking…';
 try{const raw=bridge()?.createBooking($('#tripPackage').value,$('#tripHotel').value,$('#tripTransport').value,$('#tripPeople').value);const r=JSON.parse(raw||'{}');result.textContent=r.message||'No response from booking service.';if(r.ok){toast('Booking #'+r.id+' created.');setTimeout(()=>route('bookings'),500);}}catch(err){result.textContent='Booking failed. Please check the selected options and database connection.';}
}
function renderTicket(bookingId){
 const data=JSON.parse(bridge()?.getBookingsData()||'[]');const b=data.find(x=>Number(x.id)===Number(bookingId));
 if(!b){toast('Ticket not found for this account.');return;}
 $('#pageTitle').textContent='E-Ticket / Booking Confirmation';
 $('#content').innerHTML=`<section class="module-hero"><div><span class="kicker">BOOKING CONFIRMATION</span><h2>E-Ticket #${b.id}</h2><p>Your ticket details from the saved booking record.</p></div><button class="btn btn-light" onclick="route('bookings')">← My Bookings</button></section><article class="ticket-card"><div class="ticket-head"><div><h2>TRAVEL & TOURISM MANAGEMENT SYSTEM</h2><p>OFFICIAL BOOKING CONFIRMATION</p></div><span class="booking-status">${escapeHtml(b.status)}</span></div><div class="ticket-route"><div><small>BOOKING DATE</small><strong>${escapeHtml(b.date||'—')}</strong></div><div><small>BOOKING REFERENCE</small><strong>#${b.id}</strong></div></div><div class="ticket-grid"><div><small>PACKAGE</small><strong>${escapeHtml(b.packageName||("#"+b.packageId))}</strong></div><div><small>HOTEL</small><strong>${escapeHtml(b.hotelName||("#"+(b.hotelId||"—")))}</strong></div><div><small>PASSENGERS</small><strong>${b.people}</strong></div><div><small>TRANSPORT</small><strong>${escapeHtml(b.transportName||"Not selected")}</strong></div></div><div class="ticket-route"><div><small>TRAVELER</small><strong>${escapeHtml(b.customerName||"Signed-in customer")}</strong></div><div><small>DESTINATION / TRAVEL DATE</small><strong>${escapeHtml(b.destination||"—")} · ${escapeHtml(b.travelDate||"—")}</strong></div></div><div class="ticket-total"><span>Total amount</span><strong>₹${Number(b.amount||0).toLocaleString('en-IN',{maximumFractionDigits:2})}</strong></div><div class="trip-actions"><button class="btn btn-primary" onclick="downloadETicket(${Number(b.id)})">Download E-Ticket</button><button class="btn btn-light" onclick="route('bookings')">Back to Bookings</button></div></article>`;
}
function downloadETicket(bookingId){
 const data=JSON.parse(bridge()?.getBookingsData()||'[]');
 const b=data.find(x=>Number(x.id)===Number(bookingId));
 if(!b){toast('Ticket data is unavailable for this booking.');return;}
 const money=Number(b.amount||0).toLocaleString('en-IN',{minimumFractionDigits:2,maximumFractionDigits:2});
 const html=`<!doctype html><html><head><meta charset="utf-8"><title>E-Ticket-${Number(b.id)}</title><style>
 body{font-family:Arial,sans-serif;color:#12334b;margin:36px;background:#f4f8fc}
 .ticket{max-width:780px;margin:auto;background:white;border:1px solid #d8e4ef;border-radius:18px;overflow:hidden}
 header{background:#103b56;color:white;padding:26px}header h1{font-size:22px;margin:0 0 8px}header p{margin:0;color:#d7e9f4}
 .status{float:right;background:#e9faf3;color:#08734d;padding:8px 12px;border-radius:30px;font-weight:bold}
 main{padding:28px}.grid{display:grid;grid-template-columns:1fr 1fr;gap:22px;padding:18px 0;border-bottom:1px dashed #c9d8e5}
 .grid div{min-width:0}small{display:block;color:#6c8395;font-weight:bold;font-size:11px;letter-spacing:.06em;margin-bottom:7px}strong{overflow-wrap:anywhere}
 .total{display:flex;justify-content:space-between;background:#effaf4;border:1px solid #b8e5ca;border-radius:12px;padding:20px;margin-top:22px;color:#145c3a;font-size:18px}
 footer{padding:18px 28px;color:#75899a;font-size:11px}@media print{body{background:white;margin:0}.ticket{border:0}}
 </style></head><body><article class="ticket"><header><span class="status">${escapeHtml(b.status||'UNKNOWN')}</span><h1>TRAVEL &amp; TOURISM MANAGEMENT SYSTEM</h1><p>OFFICIAL E-TICKET / BOOKING CONFIRMATION</p></header><main>
 <div class="grid"><div><small>BOOKING REFERENCE</small><strong>#${Number(b.id)}</strong></div><div><small>BOOKING DATE</small><strong>${escapeHtml(b.date||'—')}</strong></div>
 <div><small>TRAVELER</small><strong>${escapeHtml(b.customerName||'Signed-in customer')}</strong></div><div><small>EMAIL</small><strong>${escapeHtml(b.customerEmail||'—')}</strong></div>
 <div><small>PACKAGE</small><strong>${escapeHtml(b.packageName||('#'+b.packageId))}</strong></div><div><small>DESTINATION / TRAVEL DATE</small><strong>${escapeHtml(b.destination||'—')} / ${escapeHtml(b.travelDate||'—')}</strong></div>
 <div><small>HOTEL</small><strong>${escapeHtml(b.hotelName||'Not selected')}</strong></div><div><small>HOTEL LOCATION</small><strong>${escapeHtml(b.hotelLocation||'—')}</strong></div>
 <div><small>TRANSPORT</small><strong>${escapeHtml(b.transportName||'Not selected')}</strong></div><div><small>PASSENGERS</small><strong>${Number(b.people)||0}</strong></div></div>
 <div class="total"><span>Total amount</span><strong>₹${money}</strong></div></main><footer>Generated from the saved booking record. Booking/payment status is shown as stored in the system.</footer></article></body></html>`;
 try{
  const filename=`E-Ticket-Booking-${Number(b.id)}.html`;
  const nativeResult=bridge()?.saveTicketHtml?.(html,filename);
  if(nativeResult){
   const saved=JSON.parse(nativeResult);
   if(saved.ok){toast(saved.message||'E-Ticket saved in Downloads.');return;}
   toast(saved.message||'Could not save the E-Ticket.');return;
  }
  const blob=new Blob([html],{type:'text/html;charset=utf-8'});const url=URL.createObjectURL(blob);
  const a=document.createElement('a');a.href=url;a.download=filename;document.body.appendChild(a);a.click();a.remove();
  setTimeout(()=>URL.revokeObjectURL(url),1500);toast('E-Ticket downloaded. Open the HTML file in a browser to print or save as PDF.');
 }catch(err){toast('Could not download the E-Ticket. Please try again.');}
}
function renderBookings(name='Traveler',role='CUSTOMER'){
 currentRole=role||'CUSTOMER'; $('#profileName').textContent=name||'Traveler'; renderNav(currentRole);
 $('#navLinks').querySelectorAll('.nav-item').forEach(el=>el.classList.toggle('active',el.dataset.key==='bookings'));
 $('#pageTitle').textContent='My Bookings';
 $('#content').innerHTML=`<section class="module-hero"><div><span class="kicker">YOUR TRAVEL ACTIVITY</span><h2>My Bookings</h2><p>Review reservations, check their status and open your ticket.</p></div><button class="btn btn-light" onclick="route('dashboard')">← Dashboard</button></section><div class="module-toolbar"><div><h2>Your reservations</h2><p>Live records · MySQL database</p></div><input id="bookingSearch" class="module-search" placeholder="Search booking ID or status..." oninput="filterBookings()"><button class="btn btn-primary" onclick="loadBookings()">Refresh</button></div><div id="bookingResults" class="booking-list"><div class="module-loading">Loading your bookings…</div></div><div class="notice">Booking records are loaded for your signed-in account. Cancellation uses the existing booking service.</div>`;
 loadBookings();
}
function loadBookings(){
 const target=$('#bookingResults'); if(!target)return; target.innerHTML='<div class="module-loading">Loading your bookings…</div>';
 try{const raw=bridge()?.getBookingsData();const data=JSON.parse(raw||'[]');if(data.error){target.innerHTML=`<div class="module-empty">Could not load bookings: ${escapeHtml(data.error)}</div>`;return;}window.bookingRows=data;renderBookingCards(data);}catch(e){target.innerHTML='<div class="module-empty">Unable to load bookings. Check the database connection.</div>';}
}
function renderBookingCards(rows){const target=$('#bookingResults');if(!target)return;if(!rows||!rows.length){target.innerHTML='<div class="module-empty">No bookings found yet. Choose “Book a Trip” to plan your first journey.</div>';return;}target.innerHTML=rows.map(b=>{const status=String(b.status||'UNKNOWN');const cancelled=status.toUpperCase()==='CANCELLED';return `<article class="booking-card"><div class="booking-card-main"><div class="booking-card-title"><span class="data-icon">▦</span><div><h3>Booking #${b.id}</h3><p>Booked on ${escapeHtml(b.date||'Date unavailable')}</p></div></div><span class="booking-status ${cancelled?'cancelled':'confirmed'}">${escapeHtml(status)}</span></div><div class="booking-card-stats"><div><small>TRAVELLERS</small><strong>${Number(b.people)||0}</strong></div><div><small>TOTAL AMOUNT</small><strong>₹${Number(b.amount||0).toLocaleString('en-IN',{maximumFractionDigits:2})}</strong></div><div><small>PACKAGE</small><strong>${escapeHtml(b.packageName||(Number(b.packageId)>0?'#'+b.packageId:'—'))}</strong></div><div><small>HOTEL</small><strong>${escapeHtml(b.hotelName||(Number(b.hotelId)>0?'#'+b.hotelId:'—'))}</strong></div></div><div class="booking-card-actions"><button class="btn btn-primary" onclick="openBookingTicket(${Number(b.id)})">View Ticket</button>${!cancelled?`<button class="btn btn-danger" onclick="cancelBooking(${Number(b.id)})">Cancel Booking</button>`:''}</div></article>`;}).join('');}
function filterBookings(){const q=($('#bookingSearch')?.value||'').toLowerCase();renderBookingCards((window.bookingRows||[]).filter(b=>[String(b.id),b.date,b.status,String(b.packageId),String(b.hotelId)].some(v=>String(v||'').toLowerCase().includes(q))));}
function cancelBooking(id){if(!confirm('Cancel booking #'+id+'? The booking status will be updated and its reserved package seats, hotel rooms and transport seats will be restored.'))return;try{const b=bridge();if(!b){toast('Desktop bridge is not available. Restart the application.');return;}const ok=b.cancelMyBooking(Number(id));if(ok){toast('Booking cancelled and inventory restored.');loadBookings();}else toast('Cancellation failed. Check the error message and refresh your bookings.');}catch(err){toast('Cancellation failed: '+(err?.message||'Please try again.'));}}
function openBookingTicket(id){renderTicket(Number(id));}
function escapeHtml(v){return String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));}
function renderModuleCards(rows,key,config){const target=$('#moduleResults');if(!rows||!rows.length){target.innerHTML=`<div class="module-empty">No ${key} found in the database yet.</div>`;return;}target.innerHTML=rows.map(row=>`<article class="data-card"><div class="data-card-top"><span class="data-icon">${config.icon}</span><span class="data-id">ID #${row.id}</span></div><h3>${escapeHtml(row.name)}</h3><p class="data-location">⌖ ${escapeHtml(row.location||'Location not specified')}</p><p class="data-detail">${escapeHtml(row.detail||'Details not available')}</p><div class="data-card-bottom">${row.price!==null&&row.price!==undefined?`<div><small>${config.price}</small><strong>₹${Number(row.price).toLocaleString('en-IN')}</strong></div>`:''}${row.availability!==null&&row.availability!==undefined?`<span class="availability">${row.availability} ${key==='hotels'?'rooms':'seats'} available</span>`:''}</div></article>`).join('');}
function filterModuleCards(){
 const q=($('#moduleSearch')?.value||'').toLowerCase();
 const key=window.moduleKey;
 if(document.querySelector('.admin-crud-table')||document.querySelector('#moduleResults.full-table-layout')){
  document.querySelectorAll('#moduleResults tbody tr').forEach(row=>{row.hidden=!row.textContent.toLowerCase().includes(q);});
  return;
 }
 const filtered=(window.moduleRows||[]).filter(r=>[r.name,r.location,r.detail,String(r.id)].some(v=>String(v||'').toLowerCase().includes(q)));
 const config={packages:{title:'Travel Packages',icon:'✈',price:'Package price'},hotels:{title:'Hotels',icon:'▥',price:'Price per night'},destinations:{title:'Destinations',icon:'⌖',price:''}}[key];
 renderModuleCards(filtered,key,config);
}
function onBridgeReady(){if(bridge()){const role=bridge().currentRole();if(role&&role!=='GUEST')renderDashboard(bridge().currentName(),role);else renderLogin();}}
window.renderLogin=renderLogin;window.renderDashboard=renderDashboard;window.renderModule=renderModule;window.renderBookings=renderBookings;window.renderBookTrip=renderBookTrip;window.renderTicket=renderTicket;window.loginResult=loginResult;window.onBridgeReady=onBridgeReady;
renderLogin();
