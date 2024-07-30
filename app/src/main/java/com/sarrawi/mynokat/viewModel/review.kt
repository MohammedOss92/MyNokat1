package com.sarrawi.mynokat.viewModel

//الـ viewModel و الـ sharedViewModel هما نوعان من الـ ViewModel في تطبيقات أندرويد، ويختلفان في استخداماتهما:
//
//viewModel: يُستخدم لربط البيانات والعمليات بالـ Activity أو Fragment التي يمتلكها. كل ViewModel يكون مرتبطًا بنطاق الـ Activity أو Fragment التي ينتمي إليها. يتم التخلص من ViewModel عند التخلص من الـ Activity أو الـ Fragment المرتبطة به.
//
//sharedViewModel: يُستخدم عندما تحتاج إلى مشاركة البيانات بين عدة Fragments أو بين Fragment و Activity. عادةً ما يتم استخدامه في حالات تتطلب الوصول إلى نفس البيانات عبر أكثر من Fragment أو بين Activity و Fragment داخل نفس النشاط.
//
//لذلك، إذا كنت تحتاج إلى مشاركة البيانات بين أكثر من Fragment أو بين Fragment و Activity، يجب عليك استخدام sharedViewModel. أما إذا كنت تستخدمه داخل Activity أو Fragment واحد فقط، فإن viewModel سيكون كافيًا.