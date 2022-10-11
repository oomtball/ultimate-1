/*
 * MODIFIED FROM sylvan_mtbdd.c
 *
 * Copyright 2011-2016 Formal Methods and Tools, University of Twente
 * Copyright 2016-2017 Tom van Dijk, Johannes Kepler University Linz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Changes the functionality of the threshold method from sylvan_mtbdd.c
 * from returning a binary to a double MTBDD.
 */

#include "info_scce_addlib_sylvan_Sylvan.h"
#include "sylvan_threshold.h"
#include <sylvan_int.h>

TASK_IMPL_2(MTBDD, mtbdd_op_threshold_double_dleaf_helper, MTBDD, a, size_t, svalue)
{
    /* We only expect "double" terminals, or false */
    if (a == mtbdd_false) return mtbdd_false;
    if (a == mtbdd_true) return mtbdd_invalid;

    // a != constant
    mtbddnode_t na = MTBDD_GETNODE(a);

    if (mtbddnode_isleaf(na)) {
        double value = *(double*)&svalue;
        if (mtbddnode_gettype(na) == 1) {
            return mtbdd_getdouble(a) >= value ? mtbdd_double(1.0) : mtbdd_double(0.0);
        } else if (mtbddnode_gettype(na) == 2) {
            double d = (double)mtbdd_getnumer(a);
            d /= mtbdd_getdenom(a);
            return d >= value ? mtbdd_double(1.0) : mtbdd_double(0.0);
        } else {
            assert(0); // failure
        }
    }

    return mtbdd_invalid;
}

TASK_IMPL_2(MTBDD, mtbdd_threshold_double_dleaf_helper, MTBDD, dd, double, d)
{
    return mtbdd_uapply(dd, TASK(mtbdd_op_threshold_double_dleaf_helper), *(size_t*)&d);
}