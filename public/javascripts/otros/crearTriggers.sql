SELECT if(table_name in ("play_evolutions","computer", "company"),null,   
concat(
"drop trigger IF EXISTS `",table_name,"_BEFORE_INSERT` //",
"CREATE TRIGGER `",table_name,"_BEFORE_INSERT`",
"BEFORE INSERT ON `",table_name,"` FOR EACH ROW ",
"BEGIN",
"  if (new.auditinsert is null) then ",
"    set new.auditinsert = current_timestamp(); ",
"  end if; ",
"END; ",
"//",
"drop trigger IF EXISTS `",table_name,"_BEFORE_UPDATE` //",
"CREATE TRIGGER `",table_name,"_BEFORE_UPDATE` ",
"BEFORE UPDATE ON `",table_name,"` FOR EACH ROW ",
"BEGIN",
"    set new.auditlastupdate = current_timestamp(); ",
"END; ",
"//"
)   
)elTrigger
FROM information_schema.tables
where table_schema = "tv2017_dev"

