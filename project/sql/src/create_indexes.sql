CREATE INDEX hotel_names_index
ON Hotel
(hotelID);

CREATE INDEX hotel_managerUserIDs_index
ON Hotel
(managerUserID);

CREATE INDEX rooms_hotelID_index
ON Rooms
(hotelID);

CREATE INDEX rooms_roomNumber_index
ON Rooms
(roomNumber);

CREATE INDEX roomBookings_hotelID_index
ON RoomBookings
(hotelID);

CREATE INDEX roomBookings_roomNumber_index
ON RoomBookings
(roomNumber);


CREATE INDEX users_userType_index
ON Users
(userType);