use chrono::{NaiveDateTime};
use serde::{self, Deserialize, Serializer, Deserializer};

const FORMAT: &'static str = "%Y-%m-%d";

// The signature of a serialize_with function must follow the pattern:
//
//    fn serialize<S>(&T, S) -> Result<S::Ok, S::Error>
//    where
//        S: Serializer
//
// although it may also be generic over the input types T.
pub fn serialize<S>(
    date: &NaiveDateTime,
    serializer: S,
) -> Result<S::Ok, S::Error>
where
    S: Serializer,
{
    //let s = format!("{}", date.format(FORMAT));
    let s = date.timestamp() * 1000;
    //serializer.serialize_str(&s)
    serializer.serialize_i64(s)
}

// The signature of a deserialize_with function must follow the pattern:
//
//    fn deserialize<'de, D>(D) -> Result<T, D::Error>
//    where
//        D: Deserializer<'de>
//
// although it may also be generic over the output types T.
pub fn deserialize<'de, D>(
    deserializer: D,
) -> Result<NaiveDateTime, D::Error>
where
    D: Deserializer<'de>,
{
    // let s = String::deserialize(deserializer)?;
    let s = i64::deserialize(deserializer)?;
    let date = NaiveDateTime::from_timestamp_opt(s/1000, 999);
    match date {
        Some(d) => Ok(d),
        None => Err(serde::de::Error::custom("Invalid Integer to NaiveDateTime"))
    }
}