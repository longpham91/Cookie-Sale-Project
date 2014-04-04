$(function() {

  

    Morris.Donut({
        element: 'morris-donut-chart',
        data: [{
            label: "Samoas",
            value: 12
        }, {
            label: "TagAlongs",
            value: 30
        }, {
            label: "Thin Mints",
            value: 100
        }, {
            label: "DoSiDos",
            value: 120
        }, {
            label: "Trefoils",
            value: 50
        }, {
            label: "Savannah Smiles",
            value: 40
        }],
        resize: true
    });

});
