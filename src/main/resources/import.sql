INSERT
    INTO
        Todo(
            id,
            summary,
            description
        )
    VALUES(
        nextval('todo_idx'),
        'Do some stufe',
        'You should do something'
    ),
    (
        nextval('todo_idx'),
        'Do other stufe',
        'Do it now'
    );