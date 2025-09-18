/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

schedulix Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of schedulix

schedulix is free software: 
you can redistribute it and/or modify it under the terms of the 
GNU Affero General Public License as published by the 
Free Software Foundation, either version 3 of the License, 
or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
CREATE VIEW SCI_SME_QUARTER_HELP (
	JAHR,
	QUARTAL,
	ANZAHL,
	NUM_DAYS_ACTQ,
	PAST_DAYS_ACTQ
) AS
SELECT JAHR, floor((monat-1)/3+1), ANZAHL,
       DATEDIFF(str_to_date(concat(JAHR,'-',floor((monat-1)/3) * 3 + 1,'-1'),'%Y-%m-%d') + INTERVAL 3 MONTH,
                str_to_date(concat(JAHR,'-',floor((monat-1)/3) * 3 + 1,'-1'),'%Y-%m-%d')
       ),
       DATEDIFF(current_date(), str_to_date(concat(JAHR,'-',floor((monat-1)/3) * 3 + 1,'-1'),'%Y-%m-%d'))
FROM SCI_SME_COUNTER;

CREATE VIEW SCI_SME_QUARTER (
        JAHR,
        QUARTAL,
        ANZAHL,
        EXPECTED_ANZAHL,
        AVG_ANZAHL_PRO_TAG)
AS
SELECT
	JAHR,
	QUARTAL,
	SUM(ANZAHL),
	ROUND(CASE date_format(current_date(),'%Y') * 100 + (floor((date_format(current_date(),'%m')-1)/3)+1)
		WHEN JAHR*100+QUARTAL
		THEN SUM(ANZAHL * NUM_DAYS_ACTQ/PAST_DAYS_ACTQ)
		ELSE SUM(ANZAHL)
		END, 2),
	ROUND(CASE date_format(current_date(),'%Y') * 100 + (floor((date_format(current_date(),'%m')-1)/3)+1)
		WHEN JAHR*100+QUARTAL
		THEN SUM(ANZAHL / PAST_DAYS_ACTQ)
		ELSE SUM(ANZAHL / NUM_DAYS_ACTQ)
		END, 2)
FROM SCI_SME_QUARTER_HELP
GROUP BY JAHR, QUARTAL;

