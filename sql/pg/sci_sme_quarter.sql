/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software: 
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
CREATE VIEW sci_sme_quarter (
        jahr,
        quartal,
        anzahl,
        expected_anzahl,
        avg_anzahl_pro_tag)
AS
SELECT  jahr,
        ((monat-1)/3)+1,
        sum(anzahl),
	int4 (
		CASE         (int4(date_part('month', now())) - 1) / 3
			WHEN (int4(monat                    ) - 1) / 3 THEN
				sum(anzahl)
				*
				date_part( 'day', date_trunc('quarter', now()) + '3 month' - date_trunc('quarter', now())) -- anzahl tage des aktuellen quartals
				/
				date_part( 'day', now()                                    - date_trunc('quarter', now())) -- anzahl vergangene tage des aktuellen quartals
			ELSE
				sum(anzahl)
        	END
	),
	round (
		CASE         (int4(date_part('month', now())) - 1) / 3
			WHEN (int4(monat                    ) - 1) / 3 THEN
				sum(anzahl)
				/
				date_part( 'day', now()                                    - date_trunc('quarter', now())) -- anzahl vergangene tage des aktuellen quartals
			ELSE
				sum(anzahl)
				/
				date_part(
					'day',
					date_trunc('quarter', to_timestamp(jahr||'-'||(monat-1)/3+1||'-1','YYYY-MM-DD')) + '3 month' -
					date_trunc('quarter', to_timestamp(jahr||'-'||(monat-1)/3+1||'-1','YYYY-MM-DD'))
					) -- anzahl tage des quartals
        	END * 100
	) / 100
FROM sme_counter
GROUP BY jahr, (monat-1)/3;
