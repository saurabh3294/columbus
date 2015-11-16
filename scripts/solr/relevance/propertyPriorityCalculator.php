<?php

include_once 'config.php';
include_once 'staticPriorityCalculator.php';
include_once 'searchPriorityCalculator.php';

function computePropertyPriority($property) {
    global $wtSearchPriority, $wtStaticPriority, $wtDynamicPriority;
    $searchPriority = computeSearchPriority($property);
    $staticPriority = computeStaticPriority($property);
    $dynamicPriority = 100;

    $nonEditorialPriority = $wtSearchPriority * $searchPriority + $wtStaticPriority * $staticPriority + $wtDynamicPriority * $dynamicPriority;
    $editorialPriority = $property['DISPLAY_ORDER'];

    return array('editorialPriority' => $editorialPriority, 'nonEditorialPriority' => $nonEditorialPriority);
}

?>
