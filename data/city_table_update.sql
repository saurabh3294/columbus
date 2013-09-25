ALTER TABLE CITY ADD DISPLAY_PRIORITY TINYINT;

UPDATE proptiger.CITY SET DISPLAY_PRIORITY = 1 where LABEL in ('Gurgaon','Noida','Mumbai','Pune','Bangalore','Chennai','Kolkata','Hyderabad','Ahmedabad');

UPDATE proptiger.CITY SET DISPLAY_ORDER = 1 where LABEL='Gurgaon';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 2 where LABEL='Noida';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 3 where LABEL='Mumbai';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 4 where LABEL='Pune';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 5 where LABEL='Bangalore';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 6 where LABEL='Chennai';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 7 where LABEL='Kolkata';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 8 where LABEL='Hyderabad';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 9 where LABEL='Ahmedabad';

UPDATE proptiger.CITY SET DISPLAY_ORDER = 10, DISPLAY_PRIORITY=2 where LABEL='Delhi';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 11, DISPLAY_PRIORITY=2 where LABEL='Ghaziabad';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 12, DISPLAY_PRIORITY=2 where LABEL='Indore';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 13, DISPLAY_PRIORITY=2 where LABEL='Lucknow';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 14, DISPLAY_PRIORITY=2 where LABEL='Chandigarh';
UPDATE proptiger.CITY SET DISPLAY_ORDER = 15, DISPLAY_PRIORITY=2 where LABEL='Jalandhar';



