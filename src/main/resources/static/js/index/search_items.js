$('#nope').autocompleter({

    // marker for autocomplete matches
    highlightMatches: true,

    // object to local or url to remote search
    // source: colors,

    // source: null,
    // custom template
    template: '{{ label }} <span>({{ hex }})</span>',

    // show hint
    hint: false,

    // abort source if empty field
    empty: false,

    // max results
    limit: 5,
});