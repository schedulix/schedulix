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
drop view sci_sme_quarter;
CREATE VIEW sci_sme_quarter (
        jahr,
        quartal,
        anzahl,
        expected_anzahl,
        avg_anzahl_pro_tag)
AS
SELECT  jahr,
        min((monat-1)/3+1),
        sum(anzahl),

        CASE DATE(TRIM(CHAR(jahr)) + '.' + RIGHT('0' + TRIM(CHAR(min(((monat-1)/3)*3+1))),2) + '.01')
        WHEN date_trunc('quarter', DATE('today')) THEN
                    int4( sum(anzahl)/(INTERVAL('days', DATE('today') - date_trunc('quarter', DATE('today'))) + 1) *
                               INTERVAL('days', date_trunc('quarter', DATE('today')) + '3 months' - date_trunc('quarter', DATE('today')))
                        )
        ELSE sum(anzahl)
        END,

        int4(avg(anzahl))
FROM sme_counter
GROUP BY jahr, (monat-1)/3;
\g
