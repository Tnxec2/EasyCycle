package com.kontranik.easycycle.constants

import com.kontranik.easycycle.models.Phase

// Hex	RGB
//
// #4169e1	(65,105,225)
// #9400d3	(148,0,211)
// #ff69b4	(255,105,180)
// #48d1cc	(72,209,204)
// #00ee00	(0,238,0)
// #d11141	(209,17,65)
// #00b159	(0,177,89)
// #00aedb	(0,174,219)
// #f37735	(243,119,53)
// #ffc425	(255,196,37)

class DefaultPhasesData {
    companion object {
        val ar = listOf(
            Phase(
                key= 1,
                from= 1,
                to= 7,
                desc= "Tag 1-7: Monatsblutung, wenig Östrogen, Müdigkeit, achte auf zusätzliche Eisenzufuhr. Sei freundlich und sanft zu dir beim Training, fordere dich nicht heraus. Nimm ein warmes Bad oder gehe in die Sauna, das verhindert Krämpfe",
                color="#ff69b4",
                colorP="#ffd2e8",
                markwholephase = false,
            ),
            Phase(
                key= 2,
                from= 5,
                to= 7,
                desc= "Tag 5-7: Nach Ende der Blutung steigt das Östrogen. Du hast mehr Energie, bist happy und optimistisch. Testosteron steigt, Tatendrang, Zeit durchzupowern.",
            ),
            Phase(
                key= 3,
                from= 10,
                to= 15,
                desc= "Tag 10-15: Östrogen-Höhepunkt, gelblicher Schleim als Ausfluss. Östrogen macht Frauen reizbar, aber auch attraktiver. Intensivere Orgasmen, du hast evtl. weniger Hunger.",
            ),
            Phase(
                key= 4,
                from= 14,
                to= 14,
                desc= "Tag 14: Mitte des Zyklus, Eisprung, schleimiger Ausfluss. Wird das Ei nicht befruchtet, folgen plötzliche Hormonveränderungen. Das kann Gefühle intensiver werden lassen und Müdigkeit hervorrufen.",
                color= "#d11141",
                colorP= "#f1b7c6",
                markwholephase = false,
            ),
            Phase(
                key= 5,
                from= 15,
                to= 22,
                desc= "Tag 15-22: Östrogen sinkt, Testosteron und Progesteron steigen an. Fettige Haut, Pickel und empfindliche Haut.",
            ),
            Phase(
                key= 6,
                from= 21,
                to= 23,
                desc= "Tag 21-23: Stinkig? Ein Vorzeichen von PMS kann ein unangenehmer Körpergeruch sein.",
            ),
            Phase(
                key= 8,
                from= 23,
                to= 39,
                desc= "Tag 23-39: Will weg? Schokolust? Hoher Progesteronspiegel führt zu Blähungen und wenig Libido. Du hast keine Lust all das zu machen? Erlaube dir faul zu sein. Zweifel möglich, du fühlst dich träge, lustlos und gelähmt. Steigere deinen Serotonin-Spiegel mit Training. Ersetze Süßigkeiten mit gesunden Alternativen, trinke mehr Wasser.",
                color= "#00ee00",
                colorP= "#b2f9b2",
                markwholephase = true,
            ),
            Phase(
                key= 9,
                from= 40,
                desc= "Tag > 39: Wo sind deine Tage?",
                color= "#d11141",
                colorP= "#f1b7c6",
                markwholephase = false,
            )
        )
    }
}
