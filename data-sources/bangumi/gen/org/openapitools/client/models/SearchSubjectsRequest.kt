/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapitools.client.models

import org.openapitools.client.models.SearchSubjectsRequestFilter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param keyword 
 * @param sort 排序规则  - `match` meilisearch 的默认排序，按照匹配程度 - `heat` 收藏人数 - `rank` 排名由高到低 - `score` 评分 
 * @param filter 
 */


data class SearchSubjectsRequest (

    @Json(name = "keyword")
    val keyword: kotlin.String,

    /* 排序规则  - `match` meilisearch 的默认排序，按照匹配程度 - `heat` 收藏人数 - `rank` 排名由高到低 - `score` 评分  */
    @Json(name = "sort")
    val sort: SearchSubjectsRequest.Sort? = Sort.match,

    @Json(name = "filter")
    val filter: SearchSubjectsRequestFilter? = null

) {

    /**
     * 排序规则  - `match` meilisearch 的默认排序，按照匹配程度 - `heat` 收藏人数 - `rank` 排名由高到低 - `score` 评分 
     *
     * Values: match,heat,rank,score
     */
    @JsonClass(generateAdapter = false)
    enum class Sort(val value: kotlin.String) {
        @Json(name = "match") match("match"),
        @Json(name = "heat") heat("heat"),
        @Json(name = "rank") rank("rank"),
        @Json(name = "score") score("score");
    }
}
